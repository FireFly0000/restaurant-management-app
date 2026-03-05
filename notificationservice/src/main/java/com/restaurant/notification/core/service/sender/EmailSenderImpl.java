package com.restaurant.notification.core.service.sender;

import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.core.enums.NotiChannel;
import com.restaurant.notification.core.service.sender.dto.EmailPayload;
import com.restaurant.notification.core.service.sender.dto.SendResult;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailSenderImpl extends AbstractNotificationSender<EmailPayload> implements IEmailSender {
    private static final Logger _log = LoggerFactory.getLogger(EmailSenderImpl.class);
    private final JavaMailSender _sender;

    public EmailSenderImpl(
        JavaMailSender sender
    ){
        this._sender = sender;
    }

    @Override
    public NotiChannel getChannel(){
        return NotiChannel.EMAIL;
    }

    @Override
    public SendResult send(EmailPayload payload) {
        return this.sendHtml(payload);
    }

    @Override
    public SendResult sendHtml(EmailPayload payload){
        if(!payload.isValid()){
            return SendResult.failure(
                    String.join(", ", payload.getTo()),
                    getChannel().name(),
                    payload.getEventId(),
                    "Payload is invalid",
                    Constant.RES3004
            );
        }
        try{
            MimeMessage message =  _sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(payload.getFrom());
            helper.setTo(payload.getTo().toArray(new String[0]));
            helper.setSubject(payload.getSubject());
            helper.setText(payload.getBodyHtml(), true);

            if (payload.getBcc() != null && !payload.getBcc().isEmpty()) {
                helper.setBcc(payload.getBcc().toArray(new String[0]));
            }
            if (payload.getCc() != null && !payload.getCc().isEmpty()) {
                helper.setCc(payload.getCc().toArray(new String[0]));
            }

            _sender.send(message);
            _log.info("sentHtml, Sent email to: {}", String.join(", ", payload.getTo()));
            return SendResult.success(
                    String.join(", ", payload.getTo()),
                    getChannel().name(),
                    message.getMessageID()
            );
        }catch (Exception ex) {
            _log.error("sendHtml, {}", ex.getMessage());

            return SendResult.failure(
                    String.join(", ", payload.getTo()),
                    getChannel().name(),
                    ex.getMessage(),
                    payload.getEventId(),
                    Constant.RES0006
            );
        }
    }

    @Override
    public List<SendResult> sendBulk(List<EmailPayload> payloads) {
        if(payloads == null || payloads.isEmpty()){
            return new ArrayList<>();
        }
        _log.info("sendBulk, Prepare to send {} emails", payloads.size());
        List<SendResult> results = new ArrayList<>();
        List<MimeMessage> messagesToSend = new ArrayList<>();
        List<EmailPayload> emailPayloads = new ArrayList<>();

        for (EmailPayload payload : payloads) {
            if(!payload.isValid()){
                results.add(SendResult.failure(
                        "Unknown",
                        getChannel().name(),
                        payload.getEventId(),
                        "Payload is invalid or wrong type",
                        Constant.RES3004
                ));
                continue;
            }
            try{
                MimeMessage message =  _sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(payload.getFrom());
                helper.setTo(payload.getTo().toArray(new String[0]));
                helper.setSubject(payload.getSubject());
                helper.setText(payload.getBodyHtml(), true);

                if (payload.getBcc() != null && !payload.getBcc().isEmpty()) {
                    helper.setBcc(payload.getBcc().toArray(new String[0]));
                }
                if (payload.getCc() != null && !payload.getCc().isEmpty()) {
                    helper.setCc(payload.getCc().toArray(new String[0]));
                }

                messagesToSend.add(message);
                emailPayloads.add(payload);
            }catch (Exception ex){
                _log.error("sendBulk, Error creating MimeMessage: {}", ex.getMessage());

                results.add(SendResult.failure(
                        String.join(", ", payload.getTo()),
                        getChannel().name(),
                        payload.getEventId(),
                        "Error building email: " + ex.getMessage(),
                        Constant.RES0006
                ));
            }
        }

        if(!messagesToSend.isEmpty()){
            try{
                _sender.send(messagesToSend.toArray(new MimeMessage[0]));

                for(int i = 0; i < messagesToSend.size(); i++){
                    EmailPayload payload = emailPayloads.get(i);
                    MimeMessage msg = messagesToSend.get(i);

                    results.add(SendResult.success(
                            String.join(", ", payload.getTo()),
                            getChannel().name(),
                            payload.getEventId()
                    ));
                }
                _log.info("sendBulk, Sent {} emails in batch", messagesToSend.size());
            }catch (Exception ex){
                _log.error("sendBulk, Batch send failed: {}", ex.getMessage());
                for(EmailPayload  payload : emailPayloads){
                    results.add(SendResult.failure(
                            String.join(", ", payload.getTo()),
                            getChannel().name(),
                            payload.getEventId(),
                            "Batch error: " + ex.getMessage(),
                            Constant.RES0006
                    ));
                }
            }
        }
        return results;
    }

    @Override
    public SendResult sendWithAttachment(EmailPayload payload) {
        return null;
    }
}
