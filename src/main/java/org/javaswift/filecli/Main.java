package org.javaswift.filecli;

import com.beust.jcommander.JCommander;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        JCommander commander = new JCommander(arguments, args);

        if (arguments.isHelp()) {
            commander.usage();
            return;
        }

        System.out.println("Executing with "+
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
            object.uploadObject(uploadFile);
            System.out.println(object.getPublicURL());
        } else { // List files
            for (StoredObject object : container.list(arguments.getPrefix(), null, -1)) {
                System.out.println(object.getName() + " ("+ object.getContentLength()+" bytes)");
            }
        }

    }

}
