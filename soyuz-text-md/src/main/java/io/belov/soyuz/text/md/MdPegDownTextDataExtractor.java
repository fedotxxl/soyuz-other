package io.belov.soyuz.text.md;

import io.belov.soyuz.text.TextDataExtractor;
import io.belov.soyuz.text.TextData;
import io.belov.soyuz.text.TextDataFromCommentsExtractor;
import io.belov.soyuz.html.HtmlUtils;
import lombok.RequiredArgsConstructor;
import org.pegdown.ast.*;

import java.util.*;

/**
 * Created by fbelov on 05.11.15.
 */
@RequiredArgsConstructor
public class MdPegDownTextDataExtractor implements TextDataExtractor<RootNode> {

    private final TextDataFromCommentsExtractor textDataFromCommentsExtractor;

    @Override
    public TextData extract(RootNode rootNode) {
        if (rootNode == null) {
            return TextData.EMPTY;
        }

        TextData textDataFromComments = textDataFromCommentsExtractor.extract(getBeginningComments(rootNode));
        Set<String> tags = textDataFromComments.getTags();
        String title = textDataFromComments.getTitle();

        if (title == null) {
            title = getTitleFromHeaderNode(rootNode);
        }

        return new TextData(title, tags);
    }

    private List<String> getBeginningComments(RootNode rootNode) {
        List<String> answer = new ArrayList<>();

        for (Node child : rootNode.getChildren()) {
            boolean isComment = false;

            if (child instanceof HtmlBlockNode) {
                String comment = HtmlUtils.getCommentContent(((HtmlBlockNode) child).getText());
                if (comment != null) {
                    answer.add(comment);
                    isComment = true;
                }
            }

            if (!isComment) {
                break;
            }
        }

        return answer;
    }

    private String getTitleFromHeaderNode(RootNode rootNode) {
        Optional<Node> node = rootNode.getChildren().stream().filter(n -> n instanceof HeaderNode).findFirst();

        if (node.isPresent()) {
            Optional<Node> textNode = node.get().getChildren().stream().filter(n -> n instanceof TextNode).findFirst();

            if (textNode.isPresent()) {
                return ((TextNode) textNode.get()).getText();
            }
        }

        return null;
    }
}
