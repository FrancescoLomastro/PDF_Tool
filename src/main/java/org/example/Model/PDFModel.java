package org.example.Model;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.example.Exceptions.CloseDocException;
import org.example.Exceptions.SaveDocException;
import org.example.Exceptions.UnknownException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is a singleton class that represents the model of the PDF document.
 * It is responsible for loading the document, getting the pages as images, adding and removing pages, and saving the document.
 */
public class PDFModel {
    private static PDFModel instance = null;
    private String documentFilePath;
    private PDDocument document;
    private PDFRenderer renderer;

    /**
     * Private constructor to prevent instantiation of the class.
     */
    public static PDFModel getInstance() {
        if (instance == null) {
            instance = new PDFModel();
        }
        return instance;
    }
    /**
     * Sets the document file path and loads the document.
     * @param filePath The file path of the PDF document.
     * @throws IOException If an I/O error occurs.
     */
    public void setDocument(String filePath) throws IOException {
        documentFilePath = filePath;
        document = PDDocument.load(new File(documentFilePath));
        renderer = new PDFRenderer(document);
    }

    /**
     * Returns the list of page numbers in the document.
     * @return The list of page numbers.
     */
    public List<Integer> getIntegerList() {
        int totalPages = document.getNumberOfPages();
        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNumbers.add(i);
        }
        return pageNumbers;
    }

    /**
     * Returns the pages of the document as images.
     * @return The list of images.
     * @throws IOException If an I/O error occurs.
     */
    public List<byte[]> getPagesAsImages() throws UnknownException {
        int totalPages = document.getNumberOfPages();
        List<byte[]> images = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            images.add(getPageAsImage(i));
        }
        return images;
    }

    /**
     * Returns the specified page of the document as an image.
     * @param pageNumber The page number.
     * @return The image of the page.
     * @throws UnknownException If an unknown error occurs.
     */
    private byte[] getPageAsImage(int pageNumber) throws UnknownException {
        BufferedImage image = null;
        try {
            image = renderer.renderImage(pageNumber-1, 1.0f);
        } catch (IOException e) {
            throw new UnknownException(e);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            throw new UnknownException(e);
        }
        return baos.toByteArray();
    }

    /**
     * Closes the document.
     * @throws CloseDocException If an error occurs while closing the document.
     */
    public void closeDocument() throws CloseDocException {
        try {
            document.close();
        } catch (IOException e) {
            throw new CloseDocException(e);
        }
    }

    /**
     * Adds a page to the document at the specified index.
     * @param index The index at which the page should be added.
     */
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

    /**
     * Removes the page at the specified index.
     * @param pageIndex The index of the page to be removed.
     */
    public void removePage(int pageIndex) {
        document.removePage(pageIndex);
    }

    /**
     * Saves the document.
     * @throws SaveDocException If an error occurs while saving the document.
     */
    public void save() throws SaveDocException {
        try {
            document.save(documentFilePath);
        } catch (IOException e) {
            throw new SaveDocException(e);
        }
    }

    /**
     * Returns the file name of the document.
     * @return The file name.
     */
    public String getFileName() {
        return new File(documentFilePath).getName();
    }
}
