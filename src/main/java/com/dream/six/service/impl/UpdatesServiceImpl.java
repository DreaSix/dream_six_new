package com.dream.six.service.impl;

import com.dream.six.entity.Updates;
import com.dream.six.repository.UpdatesRepository;
import com.dream.six.service.UpdatesService;
import com.dream.six.vo.request.UpdatesRequestDTO;
import com.dream.six.vo.response.UpdatesResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UpdatesServiceImpl implements UpdatesService {

    private final UpdatesRepository updatesRepository;

    public UpdatesServiceImpl(UpdatesRepository updatesRepository) {
        this.updatesRepository = updatesRepository;
    }

    // Convert entity to response DTO
    private UpdatesResponseDTO mapToDTO(Updates update) {
        UpdatesResponseDTO dto = new UpdatesResponseDTO();
        dto.setId(update.getId());
        dto.setUpdateText(update.getUpdateText());
        dto.setDate(update.getCreatedAt());
        return dto;
    }

    @Override
    public List<UpdatesResponseDTO> getAllUpdates() {
        return updatesRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UpdatesResponseDTO getUpdateById(UUID id) {
        Updates update = updatesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Update not found with id: " + id));
        return mapToDTO(update);
    }

    @Override
    public UpdatesResponseDTO createUpdate(UpdatesRequestDTO requestDTO) {
        Updates update = new Updates();
        update.setUpdateText(requestDTO.getUpdateText());
        return mapToDTO(updatesRepository.save(update));
    }

    @Override
    public UpdatesResponseDTO updateUpdate(UUID id, UpdatesRequestDTO requestDTO) {
        Updates update = updatesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Update not found with id: " + id));

        update.setUpdateText(requestDTO.getUpdateText());
        return mapToDTO(updatesRepository.save(update));
    }

    @Override
    public void deleteUpdate(UUID id) {
        if (!updatesRepository.existsById(id)) {
            throw new RuntimeException("Update not found with id: " + id);
        }
        updatesRepository.deleteById(id);
    }
}

