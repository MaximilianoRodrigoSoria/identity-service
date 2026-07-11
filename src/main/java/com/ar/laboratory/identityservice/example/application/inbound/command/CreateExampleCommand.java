package com.ar.laboratory.identityservice.example.application.inbound.command;

import com.ar.laboratory.identityservice.example.domain.model.Example;

/** Puerto de entrada para crear un Example */
public interface CreateExampleCommand {

    Example execute(Example example);
}
