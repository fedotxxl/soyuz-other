package io.belov.soyuz.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import javax.annotation.Nullable;

/**
 * Created by fbelov on 05.11.15.
 */
public class HtmlUtils {

    @Nullable
    public static String getCommentContent(String html) {
        if (!isComment(html)) {
            return null;
        }

        return html.substring(4, html.length() - 3).trim();
    }

    public static String getText(String html) {
        //http://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
        return Jsoup.clean(html, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    public static boolean isComment(String html) {
        return html.startsWith("<!--") && html.endsWith("-->");
    }

}
