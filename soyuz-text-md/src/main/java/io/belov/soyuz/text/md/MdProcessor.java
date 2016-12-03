package io.belov.soyuz.text.md;

import io.belov.soyuz.text.TextProcessor;
import io.belov.soyuz.text.TextData;

import java.io.File;

/**
 * Created by fbelov on 04.11.15.
 */
public class MdProcessor implements TextProcessor {

    private MdPegDownTextDataExtractor textDataExtractor;
    private MdPegDownProvider provider;
    private MdServerProcessor mdServerProcessor;

    public MdProcessor(MdPegDownProvider provider, MdPegDownTextDataExtractor textDataExtractor, MdServerProcessor mdServerProcessor) {
        this.provider = provider;
        this.textDataExtractor = textDataExtractor;
        this.mdServerProcessor = mdServerProcessor;
    }

    @Override
    public boolean isSupported(File file) {
        return file.getName().toLowerCase().endsWith(".md");
    }

    @Override
    public Result process(String content) {
        MdPegDownProcessor processor = provider.provide();

        try {
            String htmlFromServer = mdServerProcessor.markdownToHtml(content);
            String htmlFromProcessor = processor.markdownToHtml(content); //we need to call it to return correct processor.getRootNode()
            String html = (htmlFromServer != null) ? htmlFromServer : htmlFromProcessor;

            TextData textData = textDataExtractor.extract(processor.getRootNode());

            return new Result(Status.SUCCESS, html, textData.getTitle(), textData.getTags());
        } finally {
            provider.release(processor);
        }
    }

}
