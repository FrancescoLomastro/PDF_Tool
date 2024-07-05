package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PDFModel {
    private static PDFModel instance = null;
    private String documentFilePath;
    private PDDocument document;
    private PDFRenderer renderer;


    public static PDFModel getInstance() {
        if (instance == null) {
            instance = new PDFModel();
        }
        return instance;
    }

    public void setDocument(String filePath) throws IOException {
        documentFilePath = filePath;
        document = PDDocument.load(new File(documentFilePath));
        renderer = new PDFRenderer(document);
    }

    public List<Integer> getIntegerList() {
        int totalPages = document.getNumberOfPages();
        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNumbers.add(i);
        }
        return pageNumbers;
    }

    public List<byte[]> getPagesAsImages() {
        int totalPages = document.getNumberOfPages();
        List<byte[]> images = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            images.add(getPageAsImage(i));
        }
        return images;
    }


    private byte[] getPageAsImage(int pageNumber){
        BufferedImage image = null;
        try {
            image = renderer.renderImage(pageNumber-1, 1.0f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    public void closeDocument() {
        try {
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPage(int index) {
        PDPage firstPage = document.getPage(0);
        float documentWidth = firstPage.getMediaBox().getWidth();
        float documentHeight = firstPage.getMediaBox().getHeight();
        PDRectangle pageSize = new PDRectangle(documentWidth, documentHeight);
        PDPage blankPage = new PDPage(pageSize);
        if (index == document.getNumberOfPages()) {
            document.getPages().insertAfter(blankPage, document.getPage(index-1));
        }
        else
            document.getPages().insertBefore(blankPage, document.getPage(index));
    }

    public void removePage(int pageIndex) {
        document.removePage(pageIndex);
    }

    public void save() {
        try {
            document.save(documentFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
