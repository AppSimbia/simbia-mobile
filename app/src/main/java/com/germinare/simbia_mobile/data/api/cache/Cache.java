package com.germinare.simbia_mobile.data.api.cache;

import com.germinare.simbia_mobile.data.api.model.firestore.EmployeeFirestore;
import com.germinare.simbia_mobile.data.api.model.mongo.ChatResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.IndustryResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.ProductCategoryResponse;

import java.util.ArrayList;
import java.util.List;

public class Cache {

    private static Cache instance;
    private EmployeeFirestore employee;
    private IndustryResponse industry;
    private List<ProductCategoryResponse> productCategory;
    private List<PostResponse> posts;
    private List<PostResponse> postsFiltered;
    private List<ChatResponse> chats;

    private final List<OnCacheListener> listeners = new ArrayList<>();

    public interface OnCacheListener {
        void onCacheUpdated();
    }

    public static Cache getInstance() {
        if (instance == null){
            instance = new Cache();
        }
        return instance;
    }

    public void clearCache(){
        instance = null;
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

    public void setEmployee(EmployeeFirestore employee) {
        this.employee = employee;
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

    public List<PostResponse> getPostsFiltered() {
        return postsFiltered;
    }

    public void setPostsFiltered(List<PostResponse> postsFiltered) {
        this.postsFiltered = postsFiltered;
        notifyListeners();
    }

    public List<ChatResponse> getChats() {
        return chats;
    }

    public void setChats(List<ChatResponse> chats) {
        this.chats = chats;
        notifyListeners();
    }
}
