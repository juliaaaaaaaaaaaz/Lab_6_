package org.example.server;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Acknowledgement implements Serializable {
    private final int packetNumber;

    public Acknowledgement(int packetNumber) {
        this.packetNumber = packetNumber;
    }

}
