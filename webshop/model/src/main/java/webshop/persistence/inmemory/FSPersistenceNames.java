package webshop.persistence.inmemory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * Utility to create consistent naming for file system persistence.
 * 
 * This implementation use the simple class name to create a sub path under the
 * file system database root. The resulting file names have the pattern.
 * @author hom
 */
class FSPersistenceNames {
    public static final String root="fsdb";
    static FileAttribute<Set<PosixFilePermission>> perms = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-x---"));
    
    /**
     * Create a file.
     * The directory part of the path, which is derived from the class name parameter, is created as a side effect if in not already exists.
     * @param clazz of the object to save in the file
     * @param id the id of the object.
     * @return a file within an existing directory.
     */
    public static File getFileFor( Class<?> clazz, long id) throws IOException {
        Path p = Paths.get( root, clazz.getSimpleName(),Long.toString( id )+".ser" );
        Files.createDirectories( p.getParent(), perms );
        return p.toFile();
    }
    
    /**
     * Get the designated persistence path for a class.
     * @param clazz to persist or load
     * @return the directory as path.
     */
    public static Path getDirFor(Class<?> clazz){ 
        Path p = Paths.get( root, clazz.getSimpleName());
        return p;
    }
}
