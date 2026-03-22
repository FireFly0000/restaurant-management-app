//package com.restaurant.notification.core.service.sender;
//
//import com.restaurant.commons.core.enums.NotiChannel;
//import com.restaurant.notification.core.service.sender.dto.PushPayload;
//import com.restaurant.notification.core.service.sender.dto.SendResult;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//public class PushSenderImpl extends AbstractNotificationSender implements IPushSender {
//
//    @Override
//    public NotiChannel getChannel(){
//        return NotiChannel.EMAIL;
//    }
//
//    @Override
//    public SendResult sendToDevice(String fcmToken, PushPayload payload) {
//        return null;
//    }
//
//    @Override
//    public List<SendResult> sendToUser(UUID userId, PushPayload payload) {
//        return List.of();
//    }
//
//    @Override
//    public List<SendResult> sendToUsers(List<UUID> userIds, PushPayload payload) {
//        return List.of();
//    }
//}
