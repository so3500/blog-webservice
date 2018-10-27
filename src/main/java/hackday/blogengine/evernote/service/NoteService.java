package hackday.blogengine.evernote.service;
import com.evernote.auth.EvernoteService;
import hackday.blogengine.evernote.dto.Evernote;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.evernote.auth.EvernoteAuth;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class NoteService {

    @Value("${evernote.auth.token}")
    private String AUTH_TOKEN;

    private UserStoreClient userStore;
    private NoteStoreClient noteStore;

    public List<Evernote> getAll() {
        List<Evernote> evernotes = new ArrayList<>();
        try{
            initEvernoteService(AUTH_TOKEN);
            List<String> noteGuids = getNoteGuids();
            evernotes = getNotesByGuid(noteGuids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return evernotes;
    }

    private void initEvernoteService(String token) throws Exception {
        // Set up the UserStore client and check that we can speak to the server
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
        ClientFactory factory = new ClientFactory(evernoteAuth);
        userStore = factory.createUserStoreClient();

        boolean versionOk = userStore.checkVersion("Evernote EDAMDemo (Java)",
                com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
                com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
        if (!versionOk) {
            System.err.println("Incompatible Evernote client protocol version");
            System.exit(1);
        }

        // Set up the NoteStore client
        noteStore = factory.createNoteStoreClient();
    }

    /**
     * Retrieve and display a list of the user's notes.
     */
    private List<String> getNoteGuids() throws Exception {
        // First, get a list of all notebooks
        List<Notebook> notebooks = noteStore.listNotebooks();
        List<String> noteGuids = new ArrayList<>();
        for (Notebook notebook : notebooks) {
            // Next, search for the first 100 notes in this notebook, ordering
            // by creation date
            NoteFilter filter = new NoteFilter();
            filter.setNotebookGuid(notebook.getGuid());
            filter.setOrder(NoteSortOrder.CREATED.getValue());
            filter.setAscending(true);

            NoteList noteList = noteStore.findNotes(filter, 0, 100);
            List<Note> notes = noteList.getNotes();
            for (Note note : notes) {
                noteGuids.add(note.getGuid());
            }
        }
        return noteGuids;
    }

    private List<Evernote> getNotesByGuid(List<String> noteGuids) throws Exception {
        List<Evernote> evernotes = new ArrayList<>();
        for (String noteGuid : noteGuids) {
            Note note = noteStore.getNote(noteGuid, true, true, true, true);
            evernotes.add(new Evernote(note.getTitle(), parsingHtml(note.getContent())));
        }
        return evernotes;
    }

    private String parsingHtml(String content) {
        Document doc = Jsoup.parse(content);
        return doc.text();
    }
}
