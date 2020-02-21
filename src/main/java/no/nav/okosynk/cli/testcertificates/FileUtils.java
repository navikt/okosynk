package no.nav.okosynk.cli.testcertificates;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

final class FileUtils {

  private FileUtils() {
  }

  static File putInTempFile(final InputStream data) {
    return putInTempFile(data, "test");
  }

  private static File putInTempFile(
      final InputStream data,
      final String navnPrefix) {

    if (data == null) {
      throw new IllegalArgumentException("InputStream==null. Nothing to write to temporary file");
    } else {
      try {
        final File tempFile =
            File.createTempFile(navnPrefix + System.currentTimeMillis(), ".tmp");
        tempFile.deleteOnExit();
        final OutputStream out = new FileOutputStream(tempFile);
        Throwable var4 = null;

        try {
          IOUtils.copy(data, out);
        } catch (Throwable var14) {
          var4 = var14;
          throw var14;
        } finally {
          if (out != null) {
            if (var4 != null) {
              try {
                out.close();
              } catch (Throwable var13) {
                var4.addSuppressed(var13);
              }
            } else {
              out.close();
            }
          }

        }

        return tempFile;
      } catch (IOException var16) {
        throw new RuntimeException(var16.getMessage(), var16);
      }
    }
  }
}
