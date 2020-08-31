package org.codesdream.asr.component.datamanager;

import org.codesdream.asr.exception.innerservererror.RuntimeIOException;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class ObjectStreamGenerator {

    public InputStream getSteam(Serializable serializable){
        try{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(outputStream);

            stream.writeObject(serializable);

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeIOException(e.getMessage());
        }
    }

    public Serializable getObject(InputStream stream){
        try{

            ObjectInputStream inputStream = new ObjectInputStream(stream);

            return (Serializable) inputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeIOException(e.getMessage());
        }
    }
}
