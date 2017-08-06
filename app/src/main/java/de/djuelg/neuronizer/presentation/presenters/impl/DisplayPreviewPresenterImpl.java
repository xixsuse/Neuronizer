package de.djuelg.neuronizer.presentation.presenters.impl;

import com.fernandocejas.arrow.collections.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.djuelg.neuronizer.domain.comparator.PositionComparator;
import de.djuelg.neuronizer.domain.executor.Executor;
import de.djuelg.neuronizer.domain.executor.MainThread;
import de.djuelg.neuronizer.domain.interactors.preview.DisplayPreviewInteractor;
import de.djuelg.neuronizer.domain.interactors.preview.EditTodoListInteractor;
import de.djuelg.neuronizer.domain.interactors.preview.impl.DisplayPreviewInteractorImpl;
import de.djuelg.neuronizer.domain.interactors.preview.impl.EditTodoListInteractorImpl;
import de.djuelg.neuronizer.domain.model.preview.TodoList;
import de.djuelg.neuronizer.domain.model.preview.TodoListPreview;
import de.djuelg.neuronizer.domain.repository.PreviewRepository;
import de.djuelg.neuronizer.presentation.presenters.DisplayPreviewPresenter;
import de.djuelg.neuronizer.presentation.presenters.base.AbstractPresenter;
import de.djuelg.neuronizer.presentation.ui.flexibleadapter.TodoListPreviewViewModel;

/**
 * Created by dmilicic on 12/13/15.
 */
public class DisplayPreviewPresenterImpl extends AbstractPresenter implements DisplayPreviewPresenter,
        DisplayPreviewInteractor.Callback, EditTodoListInteractor.Callback {

    private DisplayPreviewPresenter.View mView;
    private PreviewRepository mPreviewRepository;

    public DisplayPreviewPresenterImpl(Executor executor, MainThread mainThread,
                                       View view, PreviewRepository previewRepository) {
        super(executor, mainThread);
        mView = view;
        mPreviewRepository = previewRepository;
    }

    @Override
    public void resume() {
        // initialize the interactor
        DisplayPreviewInteractor interactor = new DisplayPreviewInteractorImpl(
                mExecutor,
                mMainThread,
                this,
                mPreviewRepository
        );

        // run the interactor
        interactor.execute();
    }

    @Override
    public void pause() {
        // Nothing to do
    }

    @Override
    public void stop() {
        // Nothing to do
    }

    @Override
    public void destroy() {
        // Nothing to do
    }

    @Override
    public void onPreviewsRetrieved(List<TodoListPreview> previews) {
        Collections.sort(previews, new PositionComparator());
        List<TodoListPreviewViewModel> previewVMs = new ArrayList<>();
        for (TodoListPreview preview : previews) {
            Collections.sort(preview.getItems(), new PositionComparator());
            previewVMs.add(new TodoListPreviewViewModel(preview));
        }
        mView.onPreviewsLoaded(previewVMs);
    }

    @Override
    public void syncTodoLists(List<TodoListPreviewViewModel> previews) {
        // TDOD maybe throwing Nullpointer when previews are empty
        previews = Lists.reverse(previews);
        for (TodoListPreviewViewModel vm : previews) {
            EditTodoListInteractor interactor = new EditTodoListInteractorImpl(
                    mExecutor,
                    mMainThread,
                    this,
                    mPreviewRepository,
                    vm.getTodoListUuid(),
                    vm.getTodoListTitle(),
                    previews.indexOf(vm)
            );
            interactor.execute();
        }
    }

    @Override
    public void onTodoListUpdated(TodoList updatedTodoList) {
        // nothing to to
    }
}
