/*
 * Copyright 2013 Sławomir Śledź <slawomir.sledz@sof-tech.pl>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.softech.gw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
 */
public class PlaceHolderFilter {

    private final File file;
    private final Properties props;

    public PlaceHolderFilter(File file) {
        this.file = file;
        props = new Properties();
    }

    public PlaceHolderFilter read() throws Exception {

        if (!file.exists()) {
            System.out.println(file.getAbsoluteFile() + " doesn't exist");
            return this;
        }

        FileReader in = null;
        try {
            in = new FileReader(file);
            props.load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return this;
    }

    public String filter(FileReader reader) throws IOException {

        StringBuilder builder = new StringBuilder();

        BufferedReader in = new BufferedReader(reader);

        String line;

        try {

            while ((line = in.readLine()) != null) {

                builder.append(line).append("\n");

            }

        } finally {

            in.close();

        }

        return filter(builder.toString());

    }

    public String filter(String content) {

        for (Map.Entry<Object, Object> e : props.entrySet()) {
            content = content.replace(String.format("${%s}", e.getKey()), e.getValue().toString());
        }

        System.out.println("DBG--->\n" + content + "\n<---DBG");
        
        return content;
    }

    public static void main(String[] args) {

        StringBuilder builder = new StringBuilder();
        builder.append("The ${Lord} of the Rings is an epic high ${fantasy} novel written by English philologist and University of Oxford professor J. R. R. Tolkien");

        PlaceHolderFilter phf = new PlaceHolderFilter(null);

        phf.props.put("Lord", "\\\\L\\O R D");
        phf.props.put("fantasy", "FANTASY");

        System.out.println(phf.filter(builder.toString()));

    }
}
