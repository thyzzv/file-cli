package org.javaswift.filecli;

import com.beust.jcommander.JCommander;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.TempUrlHashPrefixSource;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.File;

public class Main {

    public static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        final JCommander commander;
        try {
            commander = new JCommander(arguments, args);
        } catch (Exception err) {
            LOG.error(err.getMessage());
            return;
        }

        if (arguments.isHelp()) {
            commander.usage();
            return;
        }

        LOG.info("Executing with "+
                "tenant name "+arguments.getTenantName()+
                ", tenant ID "+arguments.getTenantId()+
                " and usr/pwd "+arguments.getUsername()+"/"+arguments.getPassword()+"@"+arguments.getUrl());

        Account account = new AccountFactory()
                .setUsername(arguments.getUsername())
                .setPassword(arguments.getPassword())
                .setAuthUrl(arguments.getUrl())
                .setPublicHost(arguments.getHost())
                .setTenantId(arguments.getTenantId())
                .setTenantName(arguments.getTenantName())
                .setHashPassword(arguments.getHashPassword())
                .setTempUrlHashPrefixSource(TempUrlHashPrefixSource.INTERNAL_URL_PATH)
                .createAccount();

        Container container = account.getContainer(arguments.getContainer());
        if (!container.exists()) {
            container.create();
        }

        if (arguments.isServer()) {
            startServer(arguments, container);
        } else if (arguments.getFile() != null) { // Upload file
            uploadFile(arguments, container);
        } else if (arguments.getDeleteFile() != null) { // Delete file
            deleteFile(arguments, container);
        } else { // List files
            listFiles(arguments, container);
        }

    }

    private static void startServer(final Arguments arguments, final Container container) {
        Spark.setPort(arguments.getPort());
        Spark.get(new Route("/:object") {
            @Override
            public Object handle(Request request, Response response) {
                StoredObject object = container.getObject(request.params(":object"));
                LOG.info("Drafting temp URL for "+object.getPath());
                return object.getTempGetUrl(arguments.getSeconds());
            }
        });
    }

    private static void listFiles(Arguments arguments, Container container) {
        for (StoredObject object : container.list(arguments.getPrefix(), null, -1)) {
            System.out.println("* "+object.getName() + " ("+ longToBytes(object.getContentLength())+") -> "+
                    (arguments.isShowTempUrl() ? object.getTempGetUrl(arguments.getSeconds()) : object.getPublicURL()));
        }
    }

    private static void deleteFile(Arguments arguments, Container container) {
        StoredObject object = container.getObject(arguments.getDeleteFile());
        object.delete();
        LOG.info(object.getName() +" deleted from Swift");
    }

    private static void uploadFile(Arguments arguments, Container container) {
        File uploadFile = new File(arguments.getFile());
        StoredObject object = container.getObject(uploadFile.getName());
        if (object.exists() && !arguments.isAllowOverride()) {
            LOG.error("File already exists. Upload cancelled");
            return;
        }
        object.uploadObject(uploadFile);
        System.out.println(object.getPublicURL());
    }

    public static String longToBytes(long bytesUsed) {
        String suffix = "B";
        if (bytesUsed / 1024 > 0) {
            bytesUsed /= 1024;
            suffix = "KB";
        }
        if (bytesUsed / 1024 > 0) {
            bytesUsed /= 1024;
            suffix = "MB";
        }
        if (bytesUsed / 1024 > 0) {
            bytesUsed /= 1024;
            suffix = "GB";
        }
        if (bytesUsed / 1024 > 0) {
            bytesUsed /= 1024;
            suffix = "TB";
        }
        return bytesUsed + " " + suffix;
    }

}
