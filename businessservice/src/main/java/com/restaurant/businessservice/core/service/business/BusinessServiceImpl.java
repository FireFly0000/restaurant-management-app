package com.restaurant.businessservice.core.service.business;

import com.restaurant.businessservice.common.MsgUtil;
import com.restaurant.businessservice.config.BusinessProperties;
import com.restaurant.businessservice.core.context.RequestContext;
import com.restaurant.businessservice.core.kafka.KafkaProducer;
import com.restaurant.businessservice.core.repository.IBusinessRepository;
import com.restaurant.businessservice.core.rpc.IBusinessServiceRpcClient;
import com.restaurant.businessservice.core.service.business.dto.CreateBusinessRequest;
import com.restaurant.businessservice.core.service.business.dto.UpdateBusinessRequest;
import com.restaurant.businessservice.core.service.business.dto.UploadFileRequest;
import com.restaurant.businessservice.model.Business;
import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.UserContext;
import com.restaurant.commons.core.enums.BusinessType;
import com.restaurant.commons.core.enums.FileCategory;
import com.restaurant.commons.core.rpc.storage.DeleteFileEvent;
import com.restaurant.commons.core.rpc.storage.PathFileSaved;
import com.restaurant.commons.exception.AppException;
import com.restaurant.commons.utils.AppUtils;
import com.restaurant.commons.utils.FileUtils;
import com.restaurant.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class BusinessServiceImpl implements IBusinessService {
    private static final Logger _log = LoggerFactory.getLogger(BusinessServiceImpl.class);
    private final IBusinessRepository _repo;
    private final IBusinessServiceRpcClient _businessRpcClient;
    private final MsgUtil _msgUtil;
    private final KafkaProducer _kafka;
    private final BusinessProperties _properties;

    public BusinessServiceImpl(
            IBusinessRepository _repo,
            MsgUtil _msgUtil,
            IBusinessServiceRpcClient _businessRpcClient,
            KafkaProducer _kafka,
            BusinessProperties _properties
    ) {
        this._repo = _repo;
        this._msgUtil = _msgUtil;
        this._businessRpcClient = _businessRpcClient;
        this._kafka = _kafka;
        this._properties = _properties;
    }

    @Override
    public Business findById(UUID id) {
        Optional<Business> obj = _repo.findById(id);
        if (obj.isPresent()) {
            _log.debug("findById, Business found with id = {}", id);
            return obj.get();
        }
        _log.warn("findById, Business not found with id = {}", id);
        return null;
    }

    @Override
    public Business getById(UUID id) {
        Optional<Business> obj = _repo.getByIdActiveTrueAndDeleteFalse(id);
        if (obj.isPresent()) {
            _log.debug("findById, Business found with id = {}", id);
            return obj.get();
        }
        _log.warn("findById, Business not found with id = {}", id);
        return null;
    }

    @Override
    public Business getByIdAndThrow(UUID id) {
        Optional<Business> obj = _repo.getByIdActiveTrueAndDeleteFalse(id);
        if (obj.isPresent()) {
            _log.debug("findById, Business found with id = {}", id);
            return obj.get();
        }
        _log.warn("findById, Business not found with id = {}", id);
        throw new AppException(_msgUtil.getMessage("business.resource.not_found"), Constant.RES3001,"400");
    }

    @Override
    public Boolean existByIdAndOwnerId(UUID id, UUID ownerId) {
        return _repo.existsByIdAndUserId(id, ownerId);
    }

    @Override
    @Transactional
    public Business create(CreateBusinessRequest request) {
        _log.info("create, Start creating Business");
        UserContext userCtx = RequestContext.get();
        Business business = Business.builder()
                .userId(UUID.fromString(userCtx.getUserId()))
                .name(request.getName())
                .description(request.getDescription())
                .websiteUrl(request.getWebsiteUrl())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .businessType(BusinessType.valueOf(request.getBusinessType()))
                .build();

        // Save to get UUID
        Business createdBusiness = _repo.save(business);
        UUID businessId = createdBusiness.getId();

        // Call Storage Service via gRPC to save images into the cloud service.
        CompletableFuture<String> avatarTask = CompletableFuture.completedFuture(null);
        CompletableFuture<String> coverImgTask = CompletableFuture.completedFuture(null);
        try {
            BusinessProperties.Cloudflare.R2 r2 = _properties.getCloudflare().getR2();

            if (request.getAvatarUrl() != null) {
                avatarTask = CompletableFuture.supplyAsync(() -> {
                    String url = _businessRpcClient.createFileOnCloud(
                            request.getAvatarUrl(),
                            FileCategory.AVATAR,
                            r2.getPublicBucket(),
                            businessId.toString()
                    );
                    // Throw to catch
                    if (url == null) throw new RuntimeException();
                    return url;
                });
            }

            if (request.getCoverImg() != null) {
                coverImgTask = CompletableFuture.supplyAsync(() -> {
                    String url = _businessRpcClient.createFileOnCloud(
                            request.getCoverImg(),
                            FileCategory.COVER_IMAGE,
                            r2.getPublicBucket(),
                            businessId.toString()
                    );
                    // Throw to catch
                    if (url == null) throw new RuntimeException();
                    return url;
                });
            }

            CompletableFuture.allOf(avatarTask, coverImgTask).join();

            business.setAvatarUrl(avatarTask.join());
            business.setCoverImgUrl(coverImgTask.join());

            Business saved = this.save(business);
            _log.info("create, Business saved with id = {}", saved.getId());
            return saved;
        } catch (Exception ex) {
            _log.error("create, Create Business failed: {}", ex.getMessage());
            // Cleanup file
            String avatarUrl = AppUtils.getTaskResult(avatarTask);
            String coverImgUrl = AppUtils.getTaskResult(coverImgTask);

            this.fileCleanUp(userCtx.getUserId(), avatarUrl, coverImgUrl);
            throw new AppException(_msgUtil.getMessage("business.create.fail"), Constant.RES3005, "400");
        }
    }

    @Override
    @Transactional
    public Business update(UUID id, UpdateBusinessRequest request) {
        _log.info("update, Start updating Business with id = {}", id);
        Business business = this.getByIdAndThrow(id);
        business.setName(request.getName());
        business.setDescription(request.getDescription());
        business.setWebsiteUrl(request.getWebsiteUrl());
        business.setPhoneNumber(request.getPhoneNumber());
        business.setEmail(request.getEmail());
        business.setBusinessType(BusinessType.valueOf(request.getBusinessType()));

        Business saved = this.save(business);
        _log.info("update, Business updated with id = {}", saved.getId());
        return saved;
    }

    @Override
    public Boolean active(UUID id) {
        _log.info("active, Start activating Business with id = {}", id);

        Business business = this.getByIdAndThrow(id);
        business.setIsActive(true);
        this.save(business);

        _log.info("active, Business actived with id = {}", id);
        return true;
    }

    @Override
    public Boolean inactive(UUID id) {
        _log.info("active, Start inactivating Business with id = {}", id);

        Business business = this.getByIdAndThrow(id);
        business.setIsActive(false);
        this.save(business);

        _log.info("active, Business inactivated with id = {}", id);
        return true;
    }

    @Override
    public String updateCoverImage(UUID id, UploadFileRequest request) {
        _log.info("updateCoverImage, Start updating cover image of Business with id = {}", id);
        Business business = this.getByIdAndThrow(id);
        String url = null;
        BusinessProperties.Cloudflare.R2 r2 = _properties.getCloudflare().getR2();
        try{
            url = this._businessRpcClient.createFileOnCloud(
                    request.getFile(),
                    FileCategory.COVER_IMAGE,
                    r2.getPublicBucket(),
                    business.getId().toString()
            );

            // Throw to catch
            if(url == null) throw new RuntimeException();

            business.setCoverImgUrl(url);
            this.save(business);
            _log.info("updateCoverImage, Business's cover image updated with id = {}", id);
            return url;
        }catch(Exception e){
            if(url != null){
                String userId = RequestContext.get().getUserId();
                this.fileCleanUp(userId, url);
            }
            throw new AppException(_msgUtil.getMessage("business.upload_cover_image.fail"), Constant.RES3005,"400");
        }
    }

    @Override
    public String updateAvatar(UUID id, UploadFileRequest request) {
        _log.info("updateAvatar, Start updating cover image of Business with id = {}", id);
        Business business = this.getByIdAndThrow(id);
        String url = null;
        BusinessProperties.Cloudflare.R2 r2 = _properties.getCloudflare().getR2();;
        try{
            url = this._businessRpcClient.createFileOnCloud(
                    request.getFile(),
                    FileCategory.AVATAR,
                    r2.getPublicBucket(),
                    business.getId().toString()
            );

            // Throw to catch
            if(url == null) throw new RuntimeException();

            business.setCoverImgUrl(url);
            this.save(business);
            _log.info("updateAvatar, Business's cover image updated with id = {}", id);
            return url;
        }catch(Exception e){
            if(url != null){
                String userId = RequestContext.get().getUserId();
                this.fileCleanUp(userId, url);
            }
            throw new AppException(_msgUtil.getMessage("business.upload_avatar.fail"), Constant.RES3005,"400");
        }
    }

    @Override
    public Business save(Business entity) {
        return this._repo.save(entity);
    }

    @Override
    public List<Business> saveAll(Collection<Business> entities) {
        return this._repo.saveAll(entities);
    }

    private void fileCleanUp(String key, String bucketName, String... urls){
        if(urls == null || urls.length == 0) return;

        List<String> validUrls = Arrays.stream(urls).filter(StringUtils::hasText).toList();
        if (validUrls.isEmpty()) return;

        List<PathFileSaved> filesToClean = validUrls.stream()
                .map(url -> {
                    String objectKey = FileUtils.extractObjectKeyR2FromUrl(url);

                    if (objectKey == null) return null;

                    return PathFileSaved.newBuilder()
                            .setBucketName(bucketName)
                            .setObjectKey(objectKey)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
        if (!filesToClean.isEmpty()) {
            DeleteFileEvent event = DeleteFileEvent.newBuilder()
                    .addAllFiles(filesToClean)
                    .build();

            _kafka.pushStorageFileCleanUpEvent(key, event, new HashMap<>());
            _log.info("fileCleanUp, Đã bắn event xóa {} files rác lên Kafka", filesToClean.size());
        }
    }
}
