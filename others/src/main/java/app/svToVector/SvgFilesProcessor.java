package app.svToVector;

import com.android.ide.common.vectordrawable.Svg2Vector;
import com.google.common.base.Charsets;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class SvgFilesProcessor {

	private Path sourceSvgPath;
	private Path destinationVectorPath;
	private String extension;
	private String extensionSuffix;

    private String mode = "";

	public SvgFilesProcessor(String sourceSvgDirectory) {
		this(sourceSvgDirectory, sourceSvgDirectory+ File.pathSeparator + "ProcessedSVG", "xml", "");
	}

	public SvgFilesProcessor(String sourceSvgDirectory, String destinationVectorDirectory) {
		this(sourceSvgDirectory, destinationVectorDirectory, "xml", "");
	}

	public SvgFilesProcessor(String sourceSvgDirectory, String destinationVectorDirectory, String extension,
                             String extensionSuffix) {
		this.sourceSvgPath = Paths.get(sourceSvgDirectory);
		this.destinationVectorPath = Paths.get(destinationVectorDirectory);
		this.extension = extension;
		this.extensionSuffix = extensionSuffix;
	}

    public SvgFilesProcessor(String sourceSvgDirectory, String destinationVectorDirectory, String mode) {
        this(sourceSvgDirectory, destinationVectorDirectory);
        this.mode = mode;
    }

    public void process(){
		try{
			EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
			//check first if source is a directory
			if(Files.isDirectory(sourceSvgPath)){
				Files.walkFileTree(sourceSvgPath, options, Integer.MAX_VALUE, new FileVisitor<Path>() {

					public FileVisitResult postVisitDirectory(Path dir,
							IOException exc) throws IOException {
						return FileVisitResult.CONTINUE;
					}

					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs)  {
						// Skip folder which is processing svgs to xml
						if(dir.equals(destinationVectorPath)){
							return FileVisitResult.SKIP_SUBTREE;
						}
						CopyOption[] opt = new CopyOption[]{COPY_ATTRIBUTES, REPLACE_EXISTING};
						Path newDirectory = destinationVectorPath.resolve(sourceSvgPath.relativize(dir));
						try{
                            Files.createDirectories(newDirectory);
						} catch(FileAlreadyExistsException ex){
                            ex.printStackTrace();
							System.out.println("FileAlreadyExistsException "+ex.toString());
						} catch(IOException x){
                            x.printStackTrace();
                            System.out.println("SKIP_SUBTREE  "+x.toString());
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
							IOException exc) throws IOException {
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
			String targetFile =CommonUtil.getFileWithXMlExtension(target, extension, extensionSuffix);
			FileOutputStream fileOutputStream = new FileOutputStream(targetFile);

            Svg2Vector.parseSvgToXml(source.toFile(), fileOutputStream);
            try {
                if(mode.equals("dark"))
                    updatePath(targetFile,"android:strokeColor","#000");
                else if (mode.equals("light")) {
                    updatePath(targetFile,"android:strokeColor","#fff");
                }
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
		} else {
			System.out.println("Skipping file as its not svg "+source.getFileName().toString());
		}
    }

    private static void updatePath(String xmlPath, String key, String value) throws DocumentException, IOException {
        Document aDocument = CommonUtil.getDocument(xmlPath);
        String keyWithoutNameSpace = key.substring(key.indexOf(":")+1,key.length());
        for(Element e : aDocument.getRootElement().elements("path")){
            Attribute attr = e.attribute(keyWithoutNameSpace);
            if(attr!=null){
                attr.setValue(String.valueOf(value));
            }else {
                e.addAttribute(key, String.valueOf(value));
            }
        }
        updateDocumentToFile(aDocument,xmlPath);
    }
    private static void updateDocumentToFile(Document outDocument, String outputConfigPath) throws IOException {
        FileWriter fileWriter = new FileWriter(outputConfigPath);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(Charsets.UTF_8.name());
        XMLWriter writer = new XMLWriter(fileWriter, format);
        writer.write(outDocument);
        writer.close();
    }


}
