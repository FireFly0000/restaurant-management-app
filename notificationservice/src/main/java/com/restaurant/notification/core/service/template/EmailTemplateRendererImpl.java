package com.restaurant.notification.core.service.template;

import com.restaurant.notification.core.kafka.UserEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailTemplateRendererImpl implements IEmailTemplateRenderer {
    private final TemplateEngine _templateEngine;
    private static final Logger _log = LoggerFactory.getLogger(EmailTemplateRendererImpl.class);

    public EmailTemplateRendererImpl(TemplateEngine templateEngine) {
        this._templateEngine = templateEngine;
    }

    @Override
    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        String res = _templateEngine.process("email/" + templateName, context);

        _log.info("render, render {} template, value {}", templateName, res);

        return res;
    }
}