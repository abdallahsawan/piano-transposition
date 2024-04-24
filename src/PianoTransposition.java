import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PianoTransposition {

    public static void main(String[] args) {
        transpositionTone("input.json", -3);
    }

    public static void transpositionTone(String filename, int interval) {
        ObjectMapper mapper=new ObjectMapper();
        InputStream is= PianoTransposition.class.getClassLoader().getResourceAsStream(filename);
        List<List<Integer>> transpositionList = new ArrayList<>();
        try {
            List<List<Integer>> toneList = mapper.readValue(is, List.class);
            for (List<Integer> tone : toneList) {
                transpositionList.add(transpositionNote(tone.get(0), tone.get(1), interval));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File("output.json"), transpositionList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Integer> transpositionNote(final int oct, int note, int interval) {
        int newNote = note + interval;
        int newOct = oct;
        if (newNote < 1) {
            newOct += (newNote / 12) - 1;
            newNote += (oct - newOct) * 12;
        } else if (newNote > 12) {
            newOct += ((newNote / 12) - 1);
            if ((newNote % 12) > 0 ) newOct++;
            newNote -= ((newOct - oct) * 12);
        }
        if (newOct < -3 || (newOct == -3 && newNote < 10)
                || newOct > 5 || (newOct == 5 && newNote > 1)) {
            throw new RuntimeException("Outbound Piano");
        }
        List<Integer> res = new ArrayList<>();
        res.add(newOct);
        res.add(newNote);
        return res;
    }
}
