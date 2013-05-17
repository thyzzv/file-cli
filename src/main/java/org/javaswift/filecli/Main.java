package org.javaswift.filecli;

import com.beust.jcommander.JCommander;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
//                .setTenantName(arguments.getTenantName())
                .setTenant(arguments.getTenantName())
                .createAccount();

        Container container = account.getContainer(arguments.getContainer());
        if (!container.exists()) {
            container.create();
            container.makePublic();
        }

        if (arguments.getFile() != null) { // Upload file
            File uploadFile = new File(arguments.getFile());
            StoredObject object = container.getObject(uploadFile.getName());
            if (object.exists() && !arguments.isAllowOverride()) {
                LOG.error("File already exists. Upload cancelled");
                return;
            }
            object.uploadObject(uploadFile);
            System.out.println(object.getPublicURL());
        } else if (arguments.getDeleteFile() != null) { // Delete file
            StoredObject object = container.getObject(arguments.getDeleteFile());
            object.delete();
            LOG.info(object.getName() +" deleted from Swift");
        } else { // List files
            for (StoredObject object : container.list(arguments.getPrefix(), null, -1)) {
                System.out.println(object.getName() + " ("+ longToBytes(object.getContentLength())+") -> "+object.getPublicURL());
            }
        }

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
