package com.dream.six.service;


import com.dream.six.vo.request.UpdatesRequestDTO;
import com.dream.six.vo.response.UpdatesResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UpdatesService {
    List<UpdatesResponseDTO> getAllUpdates();
    UpdatesResponseDTO getUpdateById(UUID id);
    UpdatesResponseDTO createUpdate(UpdatesRequestDTO requestDTO);
    UpdatesResponseDTO updateUpdate(UUID id, UpdatesRequestDTO requestDTO);
    void deleteUpdate(UUID id);
}
