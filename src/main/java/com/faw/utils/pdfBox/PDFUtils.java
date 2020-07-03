package com.faw.utils.pdfBox;
import com.google.gson.Gson;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;

import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiazh on 2020/7/2.
 */
public class PDFUtils {
    public static List<List<String>> redTable(File file){
        String data = "";
        PDDocument document =null;
        List<List<String>> pageResults = new ArrayList<>();
        try {
            document = PDDocument.load(file);
          /*  if(document.isEncrypted()){
                try{
                    document.decrypt("");
                } catch (Exception e){
                }
            }*/
            float width = document.getPage(0).getArtBox().getWidth();
            float height = document.getPage(0).getArtBox().getHeight();
            PDFTextStripperByArea stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            stripper.setWordSeparator("|");
            stripper.setLineSeparator("#");
            //划定区域
            Rectangle rect= new Rectangle(0, 0, Float.floatToIntBits(width),Float.floatToIntBits(height) );
            stripper.addRegion("area", rect);
            PDPageTree pdPages = document.getDocumentCatalog().getPages();

            int i = 0;
            for(PDPage page : pdPages){
                stripper.extractRegions(page);
                //获取区域的text
                data = stripper.getTextForRegion("area");
                String[] datas = data.split("#");
                //对文本进行分行处理
                Gson gson = new Gson();
                List<String> result = new ArrayList<>();

                List<String> strings ;
                for( i = 0; i<datas.length; i++){
                    strings = new ArrayList<>();
                    //String[] str = datas[i].split(" "); )
                    String value = gson.toJson(datas[i]);
                    //strings.addAll(Arrays.asList(value.split("\\|")));
                    result.add(value);

                }
                pageResults.add(result);
            }
            return pageResults;

        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
