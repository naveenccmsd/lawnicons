package org.example;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.example.App.customSvgPath;
import static org.example.App.outImagePath;

public class VectorToImage {

    //Output in PNG
    private static void outputPng(TranscoderInput input, File outFile) throws IOException, TranscoderException {
        PNGTranscoder t = new PNGTranscoder();
        try (OutputStream os = new FileOutputStream(outFile)) {
            TranscoderOutput output = new TranscoderOutput(os);
            t.transcode(input, output);
        }
    }

    //Output in JPEG
    private static void outputJpg(TranscoderInput input, File outFile) throws IOException, TranscoderException {
        JPEGTranscoder t = new JPEGTranscoder();
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, .8f);
        try (OutputStream os = new FileOutputStream(outFile)) {
            TranscoderOutput output = new TranscoderOutput(os);
            t.transcode(input, output);
        }
    }

    //Output as SVG
    private static void outputSvg(TranscoderInput input, File outFile) throws IOException, TranscoderException {
        SVGTranscoder t = new SVGTranscoder();
        try (OutputStream os = new FileOutputStream(outFile)) {
            OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            TranscoderOutput output = new TranscoderOutput(writer);
            t.transcode(input, output);
        }
    }

    public static void createPNGForMIUI(List<Layout> finalObj) {
        for(Layout obj : finalObj){
            String svg = obj.getDrawable();
            String pkg = obj.getPkg();
            try {
               String pngPath =  "D:\\git\\Arcticons\\icons\\black\\" + svg+".svg";
                pngPath =  pngPath.replaceAll("ccmsd_","");
                pngPath =  pngPath.replaceAll("lawn_","");
                pngPath =  pngPath.replaceAll("tf_","");
                pngPath =  pngPath.replaceAll("arc_","");
                pngPath =  pngPath.replaceAll("frost_","");

                InputStream inputStream = new FileInputStream(pngPath);
                TranscoderInput input = new TranscoderInput(inputStream);
                PNGTranscoder pngTranscoder = new PNGTranscoder();
                //Set width, height, and region
                int width = 176;
                int height = 176;
                Rectangle rect = new Rectangle(0, 0, width, height);
                pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(rect.width));
                pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(rect.height));
                pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_AOI, rect);

                outputPng(input, new File(outImagePath + pkg+".png"));


            } catch (TranscoderException | IOException e) {
                e.printStackTrace();
                System.out.println(svg +" Skipped");
            }
        }
    }
}
