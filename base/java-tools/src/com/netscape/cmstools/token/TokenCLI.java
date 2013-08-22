// --- BEGIN COPYRIGHT BLOCK ---
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; version 2 of the License.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
// (C) 2013 Red Hat, Inc.
// All rights reserved.
// --- END COPYRIGHT BLOCK ---

package com.netscape.cmstools.token;

import java.util.Arrays;

import org.jboss.resteasy.plugins.providers.atom.Link;

import com.netscape.certsrv.token.TokenClient;
import com.netscape.certsrv.token.TokenData;
import com.netscape.cmstools.cli.CLI;
import com.netscape.cmstools.cli.SubsystemCLI;

/**
 * @author Endi S. Dewata
 */
public class TokenCLI extends CLI {

    public TokenClient tokenClient;

    public TokenCLI(SubsystemCLI subsystemCLI) {
        super("token", "Token management commands", subsystemCLI);

        addModule(new TokenAddCLI(this));
        addModule(new TokenFindCLI(this));
        addModule(new TokenModifyCLI(this));
        addModule(new TokenRemoveCLI(this));
        addModule(new TokenShowCLI(this));
    }

    public void execute(String[] args) throws Exception {

        client = parent.getClient();
        tokenClient = (TokenClient)parent.getClient("token");

        if (args.length == 0) {
            printHelp();
            System.exit(1);
        }

        String command = args[0];
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        if (command == null) {
            printHelp();
            System.exit(1);
        }

        CLI module = getModule(command);
        if (module != null) {
            module.execute(commandArgs);

        } else {
            System.err.println("Error: Invalid command \"" + command + "\"");
            printHelp();
            System.exit(1);
        }
    }

    public static void printToken(TokenData token) {
        System.out.println("  Token ID: " + token.getID());
        if (token.getUserID() != null) System.out.println("  User ID: " + token.getUserID());
        if (token.getStatus() != null) System.out.println("  Status: " + token.getStatus());
        if (token.getReason() != null) System.out.println("  Reason: " + token.getReason());
        if (token.getAppletID() != null) System.out.println("  Applet ID: " + token.getAppletID());
        if (token.getKeyInfo() != null) System.out.println("  Key Info: " + token.getKeyInfo());
        if (token.getCreateTimestamp() != null) System.out.println("  Date Created: " + token.getCreateTimestamp());
        if (token.getModifyTimestamp() != null) System.out.println("  Date Modified: " + token.getModifyTimestamp());

        Link link = token.getLink();
        if (verbose && link != null) {
            System.out.println("  Link: " + link.getHref());
        }
    }
}