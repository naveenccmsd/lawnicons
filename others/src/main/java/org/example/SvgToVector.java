package org.example;

public class SvgToVector {
    public static void main(String args[]){
            String sourceDirectory = "D:\\git\\Arcticons\\icons\\black";
            String destDirectory = "D:\\git\\lawnicons\\app\\src\\main\\res\\drawable";
        loadSvgToVector(sourceDirectory, destDirectory);
    }

    public static void loadSvgToVector(String sourceDirectory, String destDirectory) {
        if(null != sourceDirectory && !sourceDirectory.isEmpty()){
            SvgFilesProcessor processor = new SvgFilesProcessor(sourceDirectory, destDirectory);
            processor.process();
        }
    }
}
