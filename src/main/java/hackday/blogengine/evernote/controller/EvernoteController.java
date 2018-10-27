package hackday.blogengine.evernote.controller;

import java.util.List;

import hackday.blogengine.evernote.dto.Evernote;
import hackday.blogengine.evernote.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
public class EvernoteController {

    @Autowired
    private NoteService evernoteService;

    @GetMapping(value = {"/", "/evernotes"})
    public String notes(Model model) {
        List<Evernote> evernotes = evernoteService.getAll();
        model.addAttribute("evernotes", evernotes);
        model.addAttribute("size", evernotes.size());
        return "main";
    }
}
