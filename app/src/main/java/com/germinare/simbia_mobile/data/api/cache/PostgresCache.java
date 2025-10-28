package com.germinare.simbia_mobile.data.api.cache;

import com.germinare.simbia_mobile.data.api.model.firestore.EmployeeFirestore;
import com.germinare.simbia_mobile.data.api.model.postgres.IndustryResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.ProductCategoryResponse;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostgresCache {

    private static PostgresCache instance;
    private EmployeeFirestore employee;
    private IndustryResponse industry;
    private List<ProductCategoryResponse> productCategory;
    private List<PostResponse> posts;

    private final List<OnCacheListener> listeners = new ArrayList<>();

    public interface OnCacheListener {
        void onCacheUpdated();
    }

    public static PostgresCache getInstance() {
        if (instance == null){
            instance = new PostgresCache();
        }
        return instance;
    }

    public void addListener(OnCacheListener listener){
        this.listeners.add(listener);
    }

    public void removeListener(OnCacheListener listener){
        this.listeners.remove(listener);
    }

    private void notifyListeners() {
        for (OnCacheListener listener : listeners) {
            listener.onCacheUpdated();
        }
    }

    public EmployeeFirestore getEmployee() {
        return employee;
    }

    public void setEmployee(DocumentSnapshot documentSnapshot) {
        this.employee = new EmployeeFirestore(documentSnapshot);
        notifyListeners();
    }

    public IndustryResponse getIndustry() {
        return industry;
    }

    public void setIndustry(IndustryResponse industry) {
        this.industry = industry;
        notifyListeners();
    }

    public List<ProductCategoryResponse> getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(List<ProductCategoryResponse> productCategory) {
        this.productCategory = productCategory;
        notifyListeners();
    }

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
        notifyListeners();
    }
}
