package com.aceboot.health;

import com.aceboot.health.HtmlOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 为浏览器提供 HTML 格式的健康检查视图。
 */
@RestController
@RequiredArgsConstructor
public class HealthHtmlEndpoint {

    private final HealthEndpoint healthEndpoint;

    @HtmlOnly
    @GetMapping(value = "/actuator/health", produces = MediaType.TEXT_HTML_VALUE)
    public String healthAsHtml() {
        HealthComponent component = healthEndpoint.health();
        return buildDocument(component);
    }

    private String buildDocument(HealthComponent root) {
        StringBuilder html = new StringBuilder(512);
        html.append("<!DOCTYPE html>")
                .append("<html lang=\"zh\">")
                .append("<head>")
                .append("<meta charset=\"UTF-8\"/>")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>")
                .append("<title>ACE-Boot 健康检查</title>")
                .append("<style>")
                .append("body{font-family:\"Segoe UI\",Arial,sans-serif;background:#f5f7fb;color:#1f2937;margin:0;padding:2rem;}")
                .append(".health-report{max-width:960px;margin:0 auto;}")
                .append("h1{margin-bottom:1.5rem;font-size:1.75rem;color:#111827;}")
                .append(".component{background:#fff;border-radius:12px;box-shadow:0 10px 25px rgba(15,23,42,0.1);padding:1rem 1.5rem;margin-bottom:1rem;}")
                .append(".component-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:0.75rem;}")
                .append(".component-name{font-weight:600;font-size:1.1rem;}")
                .append(".status{padding:0.2rem 0.75rem;border-radius:999px;font-size:0.9rem;font-weight:600;}")
                .append(".status.up{background:#ecfdf3;color:#027a48;}")
                .append(".status.down{background:#fef3f2;color:#b42318;}")
                .append(".status.unknown{background:#fef7c3;color:#b54708;}")
                .append(".details{width:100%;border-collapse:collapse;margin-bottom:0.5rem;}")
                .append(".details th{width:25%;text-align:left;padding:0.4rem 0.5rem;color:#475467;font-weight:600;font-size:0.9rem;}")
                .append(".details td{padding:0.4rem 0.5rem;font-family:monospace;color:#1d2939;font-size:0.9rem;}")
                .append(".details tr:nth-child(odd){background:#f9fafb;}")
                .append(".details.empty{margin:0;color:#6b7280;font-style:italic;}")
                .append(".children{border-left:3px solid #e5e7eb;margin-left:0.75rem;padding-left:1rem;}")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<main class=\"health-report\">")
                .append("<h1>ACE-Boot 健康检查</h1>");
        renderComponent("Overall", root, html);
        html.append("</main>")
                .append("</body>")
                .append("</html>");
        return html.toString();
    }

    private void renderComponent(String name, HealthComponent component, StringBuilder html) {
        html.append("<section class=\"component\">")
                .append("<div class=\"component-header\">")
                .append("<span class=\"component-name\">")
                .append(escape(name))
                .append("</span>")
                .append("<span class=\"status ")
                .append(statusClass(component.getStatus()))
                .append("\">")
                .append(escape(component.getStatus().getCode()))
                .append("</span>")
                .append("</div>");

        if (component instanceof Health health) {
            appendDetails(health, html);
        }
        if (component instanceof CompositeHealth composite) {
            html.append("<div class=\"children\">");
            composite.getComponents()
                    .forEach((childName, child) -> renderComponent(childName, child, html));
            html.append("</div>");
        }
        html.append("</section>");
    }

    private void appendDetails(Health health, StringBuilder html) {
        Map<String, Object> details = health.getDetails();
        if (details.isEmpty()) {
            html.append("<p class=\"details empty\">无更多详情</p>");
            return;
        }
        html.append("<table class=\"details\"><tbody>");
        details.forEach((key, value) -> html.append("<tr>")
                .append("<th>")
                .append(escape(key))
                .append("</th>")
                .append("<td>")
                .append(escape(formatDetailValue(value)))
                .append("</td>")
                .append("</tr>"));
        html.append("</tbody></table>");
    }

    private String formatDetailValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Map<?, ?> map) {
            return map.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(", ", "{", "}"));
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ", "[", "]"));
        }
        return String.valueOf(value);
    }

    private String statusClass(Status status) {
        String code = status.getCode().toUpperCase(Locale.ROOT);
        return switch (code) {
            case "UP" -> "up";
            case "DOWN", "OUT_OF_SERVICE" -> "down";
            default -> "unknown";
        };
    }

    private String escape(Object value) {
        return HtmlUtils.htmlEscape(value == null ? "" : String.valueOf(value));
    }
}
