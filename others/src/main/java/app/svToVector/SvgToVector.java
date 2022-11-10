package app.svToVector;

public class SvgToVector {
    public static void main(String args[]){

            String sourceDirectory = "../svgs/";
            String destDarkDirectory = "../app/src/dark/res/drawable";
            String destLightDirectory = "../app/src/light/res/drawable";
            System.out.println(sourceDirectory + " => "+destDarkDirectory);
            loadSvgToVector(sourceDirectory, destDarkDirectory,"dark");
            System.out.println(sourceDirectory + " => "+destLightDirectory);
            loadSvgToVector(sourceDirectory, destLightDirectory,"light");

    }

    public static void loadSvgToVector(String sourceDirectory, String destDirectory, String mode) {
        if(null != sourceDirectory && !sourceDirectory.isEmpty()){
            SvgFilesProcessor processor = new SvgFilesProcessor(sourceDirectory, destDirectory , mode);
            processor.process();
        }
    }
}
