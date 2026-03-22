package com.restaurant.notification.core.service.template;

import java.util.Map;

public interface IEmailTemplateRenderer {
    String render(String templateName, Map<String, Object> variables);
}
