package app.lawnchair.lawnicons.helper;

import com.android.ide.common.vectordrawable.Svg2Vector;
import com.google.common.base.Charsets;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import static java.nio.file.FileVisitResult.CONTINUE;

public class SvgFilesProcessor {

	private final Path sourceSvgPath;
	private final Path destinationVectorPath;
	private final String extension;
	private final String extensionSuffix;
    private final CommonUtil commonUtil = new CommonUtil();
    private final String mode;


    public SvgFilesProcessor(String sourceSvgDirectory, String destinationVectorDirectory, String mode) {
        this.sourceSvgPath = Paths.get(sourceSvgDirectory);
        this.destinationVectorPath = Paths.get(destinationVectorDirectory);
        this.mode = mode;
        extensionSuffix = "";
        extension = "xml";
    }

    public void process(){
		try{
			EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
			//check first if source is a directory
			if(Files.isDirectory(sourceSvgPath)){
				Files.walkFileTree(sourceSvgPath, options, Integer.MAX_VALUE, new FileVisitor<>() {

					public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
						return FileVisitResult.CONTINUE;
					}

					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs)  {
						// Skip folder which is processing svgs to xml
						if(dir.equals(destinationVectorPath)){
							return FileVisitResult.SKIP_SUBTREE;
						}
                        Path newDirectory = destinationVectorPath.resolve(sourceSvgPath.relativize(dir));
						try{
                            Files.createDirectories(newDirectory);
						} catch(FileAlreadyExistsException ex){
                            ex.printStackTrace();
							System.out.println("FileAlreadyExistsException "+ ex);
						} catch(IOException x){
                            x.printStackTrace();
                            System.out.println("SKIP_SUBTREE  "+ x);
							return FileVisitResult.SKIP_SUBTREE;
						}
						return CONTINUE;
					}


					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {
						convertToVector(file, destinationVectorPath.resolve(sourceSvgPath.relativize(file)));
						return CONTINUE;
					}


					public FileVisitResult visitFileFailed(Path file,
							IOException exc) {
						return CONTINUE;
					}
				});
			} else {
				System.out.println("source not a directory");
			}

		} catch (IOException e){
            e.printStackTrace();
			System.out.println("IOException "+e.getMessage());
		}
	}

	private void convertToVector(Path source, Path target) throws IOException{
		// convert only if it is .svg
		if(source.getFileName().toString().endsWith(".svg")){
			String targetFile =commonUtil.getFileWithXMlExtension(target, extension, extensionSuffix);
            if(targetFile==null) {
                return;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(targetFile);

            Svg2Vector.parseSvgToXml(source.toFile(), fileOutputStream);
            try {
                if(mode.equals("dark")) {
                    updatePath(targetFile, "android:strokeColor", "#000");
                    updatePath(targetFile, "android:fillColor", "#000");
                } else if (mode.equals("light")) {
                    updatePath(targetFile,"android:strokeColor","#fff");
                    updatePath(targetFile, "android:fillColor", "#fff");
                }
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
		} else {
			System.out.println("Skipping file as its not svg "+ source.getFileName());
		}
    }

    private void updatePath(String xmlPath, String key, String value) throws DocumentException, IOException {
        Document aDocument = commonUtil.getDocument(xmlPath);
        String keyWithoutNameSpace = key.substring(key.indexOf(":")+1);
        if(aDocument!=null && aDocument.getRootElement()!=null) {
            for (Element e : aDocument.getRootElement().elements("path")) {
                Attribute attr = e.attribute(keyWithoutNameSpace);
                if (attr != null) {
                    if (!attr.getValue().equals("#00000000")) {
                        attr.setValue(String.valueOf(value));
                    }
                }
            }
            updateDocumentToFile(aDocument, xmlPath);
        }
    }
    private void updateDocumentToFile(Document outDocument, String outputConfigPath) throws IOException {
        FileWriter fileWriter = new FileWriter(outputConfigPath);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(Charsets.UTF_8.name());
        XMLWriter writer = new XMLWriter(fileWriter, format);
        writer.write(outDocument);
        writer.close();
    }


}
