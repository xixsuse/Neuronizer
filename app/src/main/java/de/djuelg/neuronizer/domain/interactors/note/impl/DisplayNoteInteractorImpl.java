package de.djuelg.neuronizer.domain.interactors.note.impl;

import com.fernandocejas.arrow.optional.Optional;

import de.djuelg.neuronizer.domain.executor.Executor;
import de.djuelg.neuronizer.domain.executor.MainThread;
import de.djuelg.neuronizer.domain.interactors.base.AbstractInteractor;
import de.djuelg.neuronizer.domain.interactors.note.DisplayNoteInteractor;
import de.djuelg.neuronizer.domain.model.preview.Note;
import de.djuelg.neuronizer.domain.repository.NoteRepository;

/**
 * Created by djuelg on 09.07.17.
 */
public class DisplayNoteInteractorImpl extends AbstractInteractor implements DisplayNoteInteractor {

    private final Callback callback;
    private final NoteRepository repository;
    private final String uuid;

    public DisplayNoteInteractorImpl(Executor threadExecutor, MainThread mainThread,
                                     Callback callback, NoteRepository repository, String uuid) {
        super(threadExecutor, mainThread);
        this.callback = callback;
        this.repository = repository;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        final Optional<Note> note = repository.getNoteById(uuid);
        if (note.isPresent()) {
            mMainThread.post(new Runnable() {
                @Override
                public void run() {
                    callback.onNoteRetrieved(note);
                }
            });

            final Note loadedNote = note.get().increaseAccessCounter();
            repository.update(loadedNote);
            // TODO fix don't update LastChange on Open
            // TODO fix importance normalization
            //if (loadedNote.getAccessCounter() >= ACCESS_PEAK) normalizeImportance();
        }

        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onNoteRetrieved(note);
            }
        });
    }
}