package com.acp.simccs.common.util;

public class EmailTemplateUtil {

    private static final String EMAIL_WRAPPER = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #0f172a; margin: 0; padding: 40px 20px; line-height: 1.6; }
                    .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03); }
                    .header { background: linear-gradient(135deg, #4f46e5 0%%, #6366f1 100%%); padding: 32px 40px; text-align: left; }
                    .header h2 { color: #ffffff; margin: 0; font-size: 24px; font-weight: 800; letter-spacing: 1px; text-transform: uppercase; }
                    .header p { color: #e0e7ff; margin: 8px 0 0 0; font-size: 13px; font-weight: 600; text-transform: uppercase; letter-spacing: 2px; }
                    .content { padding: 40px; }
                    .title { font-size: 20px; font-weight: 700; color: #1e293b; margin-top: 0; margin-bottom: 16px; }
                    .body-text { font-size: 15px; color: #475569; margin-bottom: 24px; }
                    .action-btn { display: inline-block; background-color: #4f46e5; color: #ffffff; font-weight: 600; text-decoration: none; padding: 12px 28px; border-radius: 8px; font-size: 14px; text-align: center; }
                    .action-btn:hover { background-color: #4338ca; }
                    .footer { padding: 24px 40px; border-top: 1px solid #e2e8f0; background-color: #f8fafc; text-align: center; font-size: 12px; color: #94a3b8; }
                    .comment-box { background-color: #f1f5f9; border-left: 4px solid #4f46e5; padding: 16px 20px; border-radius: 0 8px 8px 0; margin-bottom: 24px; font-style: italic; color: #334155; font-size: 14px; }
                    .comment-author { font-weight: 700; font-size: 12px; text-transform: uppercase; color: #64748b; margin-bottom: 8px; font-style: normal; display: block; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>SIMCCS</h2>
                        <p>Secure Information Management</p>
                    </div>
                    <div class="content">
                        %s
                    </div>
                    <div class="footer">
                        &copy; SIMCCS Intelligence Division. All rights reserved.<br/>
                        This is an automated encrypted transmission. Do not reply.
                    </div>
                </div>
            </body>
            </html>
            """;

    public static String getBasicTemplate(String title, String body, String actionUrl, String actionText) {
        String mainContent = """
                <h3 class="title">%s</h3>
                <p class="body-text">%s</p>
                <a href="%s" class="action-btn">%s</a>
                """.formatted(title, body, actionUrl, actionText);
        return EMAIL_WRAPPER.formatted(mainContent);
    }

    public static String getWorkflowTemplate(String title, String body, String actorName, String comment,
            String actionUrl, String actionText) {
        StringBuilder content = new StringBuilder();
        content.append("<h3 class=\"title\">").append(title).append("</h3>\n");
        content.append("<p class=\"body-text\">").append(body).append("</p>\n");

        if (comment != null && !comment.trim().isEmpty()) {
            content.append("<div class=\"comment-box\">\n");
            content.append("<span class=\"comment-author\">Feedback from ").append(actorName).append("</span>\n");
            content.append("\"").append(comment).append("\"\n");
            content.append("</div>\n");
        }

        content.append("<a href=\"").append(actionUrl).append("\" class=\"action-btn\">").append(actionText)
                .append("</a>\n");
        return EMAIL_WRAPPER.formatted(content.toString());
    }

    public static String getAlertTemplate(String errorDetails) {
        String mainContent = """
                <div style="border-left: 5px solid #ef4444; background: #fef2f2; padding: 20px; border-radius: 0 8px 8px 0;">
                    <h3 style="color: #991b1b; margin-top: 0; font-size: 16px; text-transform: uppercase; letter-spacing: 1px;">⚠️ System Alert</h3>
                    <p style="color: #7f1d1d; font-size: 14px; margin-bottom: 12px;">The following automated process encountered a critical failure:</p>
                    <pre style="background: #ffffff; padding: 12px; border: 1px solid #fca5a5; border-radius: 6px; overflow-x: auto; color: #450a0a; font-size: 12px;">%s</pre>
                </div>
                """
                .formatted(errorDetails);
        return EMAIL_WRAPPER.formatted(mainContent);
    }
}