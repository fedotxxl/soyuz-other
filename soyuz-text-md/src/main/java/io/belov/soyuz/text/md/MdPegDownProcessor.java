package io.belov.soyuz.text.md;

import org.pegdown.*;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

import java.util.List;
import java.util.Map;

/**
 * Dirty fix. Not thread safe!!!
 */
class MdPegDownProcessor extends PegDownProcessor {

    private RootNode rootNode;

    @Override
    public String markdownToHtml(char[] markdownSource,
                                 LinkRenderer linkRenderer,
                                 Map<String, VerbatimSerializer> verbatimSerializerMap,
                                 List<ToHtmlSerializerPlugin> plugins) {
        try {
            rootNode = parseMarkdown(markdownSource);
            return new ToHtmlSerializer(linkRenderer, verbatimSerializerMap, plugins).toHtml(rootNode);
        } catch(ParsingTimeoutException e) {
            return null;
        }
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    public void resetRootNode() {
        rootNode = null;
    }
}
