package org.wallet.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wallet.dto.OperationDTO;
import org.wallet.services.OperationService;

@RestController
@RequestMapping("/operation")
public class OperationController {

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OperationDTO operationDTO) {
        OperationDTO operationResponseDTO = operationService.createOperation(operationDTO);
        return new ResponseEntity<>(operationResponseDTO, HttpStatus.OK);
    }

}
