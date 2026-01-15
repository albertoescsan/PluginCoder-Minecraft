package berty.plugincoder.compiler;

import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.main.PluginCoder;
import berty.plugincoder.writer.PluginFileWriter;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PluginCompiler {
    public static PluginCoder mainPlugin;
    public static void compilePlugin(Plugin plugin) throws Exception {
        String filesPrefix = mainPlugin.getDataFolder().getParentFile().getPath() +
                "/PluginCoder/plugins/"+ plugin.getName();
        File pluginFolder=new File(filesPrefix);
        if(!pluginFolder.exists())pluginFolder.mkdir();
        filesPrefix+="/"+ plugin.getName();
        PluginJavaTranslator.createJavaPlugin(filesPrefix,plugin);
        PluginJavaTranslator.createPluginYML(filesPrefix,plugin);
        compressSourceCode(filesPrefix+".zip",filesPrefix);
        compileFiles(plugin);
        createJarFile(plugin);
        deletePluginFiles(plugin);
    }

    public static void deletePluginFiles(Plugin plugin){
        deleteFiles(new File(mainPlugin.getDataFolder().getParentFile().getPath()+ "/PluginCoder/plugins/"+plugin.getName()+"/"+plugin.getName()));
        deleteFiles(new File(mainPlugin.getDataFolder().getParentFile().getPath()+ "/PluginCoder/plugins/"+plugin.getName()+"/output"));
    }
    private static void deleteFiles(File rootDir) {
        if(!rootDir.exists())return;
        if(!rootDir.isDirectory()){rootDir.delete();return;}
        for(File file:rootDir.listFiles())deleteFiles(file);
        rootDir.delete();
    }

    private static void compileFiles(Plugin plugin) throws Exception {
        String sourcePath = mainPlugin.getDataFolder().getParentFile().getPath()+ "/PluginCoder/plugins/"+plugin.getName()+"/"+plugin.getName();
        String mainJavaFilePath = sourcePath + "/main/Plugin.java";
        String outputDirectoryPath = mainPlugin.getDataFolder().getParentFile().getPath()+ "/PluginCoder/plugins/"+plugin.getName()+"/output";
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            //no hay compilador
            return;
        }
        String spigotPath="spigot-api.jar"; //TODO cambiar para que descargue la dependencia de su versión
        //usar ; como separador de classpath en windows, pero es : para linux
        int compilationResult = compiler.run(null, null, null, "-cp",spigotPath + ";" + sourcePath, "-d", outputDirectoryPath, mainJavaFilePath);
        if (compilationResult == 0) {
            //exitoso
        } else {
            //error
            throw new Exception("compilation error");
        }
        try{
            byte[] bytes = Files.readAllBytes(Paths.get(sourcePath+"/plugin.yml"));
            File outputPluginYML=new File(outputDirectoryPath+"/plugin.yml");
            outputPluginYML.createNewFile();
            PluginFileWriter.writeInFile(outputPluginYML, new String(bytes, "UTF-8"));
        }catch (Exception e){}
    }
    private static void createJarFile(Plugin plugin) {
        String outputJarDirectoryPath=mainPlugin.getDataFolder().getParentFile().getPath() + "/PluginCoder/plugins/"+plugin.getName();
        String sourceDirectoryPath=mainPlugin.getDataFolder().getParentFile().getPath() + "/PluginCoder/plugins/"+plugin.getName()+"/output";
        try {
            String jarFileName = outputJarDirectoryPath + "/"+plugin.getName()+".jar";
            FileOutputStream fos = new FileOutputStream(jarFileName);
            JarOutputStream jos = new JarOutputStream(fos, new Manifest());
            File sourceDirectory = new File(sourceDirectoryPath);
            for (File file : sourceDirectory.listFiles()) {
                addFilesToJar(file, jos, "");
            }
            jos.close();
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static void addFilesToJar(File source, JarOutputStream jos, String path) throws IOException {
        if (source.isDirectory()) {
            String entryPath = path + source.getName() + "/";
            jos.putNextEntry(new JarEntry(entryPath));
            jos.closeEntry();
            for (File file : source.listFiles()) {
                addFilesToJar(file, jos, entryPath);
            }
        }else {
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(source);
            jos.putNextEntry(new JarEntry(path + source.getName()));

            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                jos.write(buffer, 0, bytesRead);
            }
            fis.close();
            jos.closeEntry();
        }
    }
    private static  void compressSourceCode(String zipPath,String sourcePath){
        try (FileOutputStream fos = new FileOutputStream(zipPath);
             ZipOutputStream zos = new ZipOutputStream(fos)){

                // Comprimir la carpeta
                File folder = new File(sourcePath);
                if (folder.exists() && folder.isDirectory()) {
                    compressCode(folder, folder.getName(), zos);
                }
            }catch (Exception e){e.printStackTrace();}
        }
    private static void compressCode(File folder, String parentPath, ZipOutputStream zos) throws Exception {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                // Recursivamente comprimir subcarpetas
                compressCode(file, parentPath + "/" + file.getName(), zos);
            } else {
                // Comprimir archivos
                addFileToZip(file, parentPath, zos);
            }
        }
    }

    // Método para agregar un archivo al ZIP
    private static void addFileToZip(File file, String parentPath, ZipOutputStream zos) throws Exception {
        String zipEntryName = parentPath.isEmpty() ? file.getName() : parentPath + "/" + file.getName();
        zos.putNextEntry(new ZipEntry(zipEntryName));

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
        }

        zos.closeEntry();
    }
}