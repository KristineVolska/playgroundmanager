package com.example.playgroundmanager.model;

/**
 * Created an enum that would allow defining more types of tickets in the future.
 * For example, a ticket, that would allow attending only certain types of playsites.
 * However, it would require adding additional validation when a kid wants to attend a playsite
 * which is not implemented yet.
 */
public enum TicketType {
    INCLUDE_ALL,
    SPECIFY_PLAYSITE
}
