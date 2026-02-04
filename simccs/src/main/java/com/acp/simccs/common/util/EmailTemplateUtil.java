package com.acp.simccs.common.util;

public class EmailTemplateUtil {

    public static String getBasicTemplate(String title, String body, String actionUrl, String actionText) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;">
                    <h2 style="color: #d9534f;">SIMCCS Notification</h2>
                    <h3>%s</h3>
                    <p>%s</p>
                    <br/>
                    <a href="%s" style="background-color: #d9534f; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
                        %s
                    </a>
                    <br/><br/>
                    <p style="font-size: 12px; color: #777;">Secure Information Management and Crisis Communication System</p>
                </div>
            </body>
            </html>
            """.formatted(title, body, actionUrl, actionText);
    }

    public static String getAlertTemplate(String errorDetails) {
        return """
            <html>
            <body style="font-family: monospace; background-color: #f4f4f4; padding: 20px;">
                <div style="border-left: 5px solid red; background: white; padding: 15px;">
                    <h3 style="color: red;">⚠️ SYSTEM ALERT</h3>
                    <p>The following system error occurred:</p>
                    <pre>%s</pre>
                </div>
            </body>
            </html>
            """.formatted(errorDetails);
    }
}