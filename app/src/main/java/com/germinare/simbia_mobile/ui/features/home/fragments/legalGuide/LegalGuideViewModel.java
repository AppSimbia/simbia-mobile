package com.germinare.simbia_mobile.ui.features.home.fragments.legalGuide;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.germinare.simbia_mobile.data.api.model.integration.LawResponse;
import com.germinare.simbia_mobile.data.api.repository.IntegrationRepository;

import java.util.List;

public class LegalGuideViewModel extends ViewModel {

    private final IntegrationRepository repository;

    private final MutableLiveData<List<LawResponse>> _lawList = new MutableLiveData<>();
    public LiveData<List<LawResponse>> getLawList() {
        return _lawList;
    }

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> getErrorMessage() {
        return _errorMessage;
    }

    public LegalGuideViewModel(IntegrationRepository repository) {
        this.repository = repository;
    }


    public void fetchLaws() {
        repository.listLaws(lawList -> {
            _lawList.setValue(lawList);
        });
    }


    public static class Factory implements ViewModelProvider.Factory {
        private final IntegrationRepository repository;

        public Factory(IntegrationRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LegalGuideViewModel.class)) {
                try {
                    return (T) new LegalGuideViewModel(repository);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unknown ViewModel class " + modelClass, e);
                }
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}