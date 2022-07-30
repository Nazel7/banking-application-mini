package com.decagon.bank.event.notifcation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.decagon.bank.dtos.request.OriginatorKyc;

import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationLog {

    private String id;
    private DataInfo data;
    private String eventType;
    private String initiator;
    private OriginatorKyc originatorKyc;
    private String tranxRef;
    private String channelCode;
    private Date tranxDate;

}
