package com.example.videomeetingapp.listeners;

import com.example.videomeetingapp.models.User;

public interface UserListener {

    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);
}
