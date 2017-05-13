package org.cugos.mapcubes;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

@Controller
public class Rest {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
    public ModelAndView home(Model model) {
        return new ModelAndView("upload");
    }

    @PostMapping("/")
    public HttpEntity<byte[]> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
        BufferedImage image = ImageIO.read(inputStream);
        BufferedImage imageWithTabs = createImageWithTabs(image, 60);
        return createHttpEntity(getBytes(imageWithTabs, type), type);
    }

    protected HttpEntity createHttpEntity(byte[] bytes, String type) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Empty Image!");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type.equalsIgnoreCase("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG);
        headers.setContentLength(bytes.length);
        return new HttpEntity<byte[]>(bytes, headers);
    }

    protected byte[] getBytes(BufferedImage image, String type) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, type, out);
        out.close();
        return out.toByteArray();
    }

    private void drawTab(Graphics2D g, int x, int y, String side, int tab) {
        int size = 300;
        // top
        if (side.equalsIgnoreCase("top")) {
            g.drawLine(x, y, x+tab, y-tab);
            g.drawLine(x + tab, y - tab, x + size - tab, y - tab);
            g.drawLine(x + size, y, x + size - tab, y - tab);
        }
        // bottom
        else if (side.equalsIgnoreCase("bottom")) {
            g.drawLine(x, y, x+tab, y+tab);
            g.drawLine(x + tab, y + tab, x + size - tab, y + tab);
            g.drawLine(x + size, y, x + size - tab, y + tab);
        }
        // east
        else if (side.equalsIgnoreCase("east")) {
            g.drawLine(x, y, x + tab, y + tab);
            g.drawLine(x + tab, y + tab, x + tab, y + size - tab);
            g.drawLine(x, y + size, x + tab, y + size - tab);
        }
    }

    protected BufferedImage createImageWithTabs(BufferedImage image, int tabSize) {
        BufferedImage image2 = new BufferedImage(1810, 1200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image2.createGraphics();
        g.setPaint(new Color(255, 255, 255));
        g.fillRect(0, 0, 1810, 1200);
        g.drawImage(image, 0, 150, null);
        g.setPaint(new Color(0, 0, 0));
        // top
        drawTab(g, 304, 454, "top", tabSize);
        drawTab(g, 304 + 300, 454 - 300, "top", tabSize);
        drawTab(g, 304 + (300 * 2), 454, "top", tabSize);
        //drawTab(g, 304 + (300 * 3), 454, "top")
        // bottom
        drawTab(g, 304, 754, "bottom", tabSize);
        drawTab(g, 304 + (300 * 2), 754, "bottom", tabSize);
        drawTab(g, 304 + (300 * 3), 754, "bottom", tabSize);
        // east
        drawTab(g, 304 + (300 * 4), 454, "east", tabSize);

        g.dispose();
        return image2;
    }

}