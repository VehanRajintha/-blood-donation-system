package com.blooddonation.dao;

import com.blooddonation.model.Request;
import java.util.List;

public interface RequestDAO {
    int addRequest(Request request);
    boolean updateRequest(Request request);
    boolean deleteRequest(int requestId);
    Request getRequest(int requestId);
    List<Request> getAllRequests();
} 