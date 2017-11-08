/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webshop.persistence.pgdb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author hom
 */
public class PGDBTestUtil {
        /**
     * Load test tables into database.
     *
     * @throws IOException when file not found
     */
    static void createTestTables( String sqlScript) throws
            IOException, SQLException {
        // create a test table from a sql script
        List<String> createScriptLines = Files.readAllLines( new File(
                sqlScript ).toPath() );
        StringBuilder scriptBuilder = new StringBuilder();
        for ( String s : createScriptLines ) {
            scriptBuilder.append( s ).append( "\n" );
        }
        String script = scriptBuilder.toString();
        System.out.println( "script = " + script );
        try ( QueryHelper qh = new QueryHelper() ) {
            qh.doDDL( script );
        }
    }

}
