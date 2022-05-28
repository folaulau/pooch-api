package com.pooch.api.entity.notification.pushnotification;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.pooch.api.entity.notification.Notification;
import com.pooch.api.entity.notification.pushnotification.token.PushNotificationToken;
import lombok.extern.slf4j.Slf4j;

/**
 * https://firebase.google.com/docs/cloud-messaging/send-message
 * 
 * @author folaukaveinga
 *
 */
@Slf4j
@Service
public class PushNotificationServiceImp implements PushNotificationService {

  @Override
  public PushNotification send(PushNotification pushNotification,
      Set<PushNotificationToken> tokens) {
    // TODO Auto-generated method stub
    return null;
  }
  //
  // @Autowired
  // private FirebaseApp firebaseApp;
  //
  // @Autowired
  // private PushNotificationRepository pushNotificationRepository;
  //
  // @Autowired
  // private PushNotificationTokenService pushNotificationTokenService;
  //
  // @Autowired
  // private MemberService memberService;
  //
  // @Autowired
  // private PhiPiiUtils phiPiiUtils;
  //
  // private PushNotification saveUpdate(PushNotification pushNotification) {
  // log.info("Saving push notification");
  // return pushNotificationRepository.saveAndFlush(pushNotification);
  // }
  //
  // @Override
  // public PushNotification send(PushNotification pushNotification, Long memberId) {
  // List<PushNotificationToken> tokens =
  // pushNotificationTokenService.getTokensByMemberId(memberId);
  // return send(pushNotification, new HashSet<>(tokens));
  // }
  //
  // @Override
  // public PushNotification send(PushNotification pushNotification, String memberUuid) {
  // long memberId = memberService.getIdByUuid(memberUuid);
  // return send(pushNotification, memberId);
  // }
  //
  // @Override
  // public PushNotification send(PushNotification pushNotification,
  // Set<PushNotificationToken> tokens) {
  // for (PushNotificationToken token : tokens) {
  // if (token.getDeviceType().equals(PushNotificationDeviceType.ANDROID)) {
  // this.sendToAndroid(pushNotification, token.getToken());
  // }
  // }
  //
  // return this.saveUpdate(pushNotification);
  // }
  //
  // public PushNotification sendToAndroid(PushNotification pushNotification, String token) {
  // log.info("Sending push notification to Android; {}",
  // phiPiiUtils.sanitizePhiPiiToString(pushNotification.getPayload()));
  // try {
  // Message message = Message.builder()
  // .putData("navigationEnum", pushNotification.getNavigationEnum())
  // .putData("modelType", pushNotification.getModelType())
  // .putData("modelUuid", pushNotification.getModelUuid())
  // .setAndroidConfig(AndroidConfig.builder().setTtl(3600 * 1000)
  //
  // .setNotification(AndroidNotification.builder().setTitle(pushNotification.getTitle())
  // .setBody(pushNotification.getDescription())
  // .setClickAction("FIREBASEPUSHNOTIFICATION").build())
  //
  // .setCollapseKey("Test_Collapse_Key").setPriority(Priority.HIGH).build())
  //
  // .setToken(token).build();
  //
  // String response = FirebaseMessaging.getInstance(firebaseApp).send(message);
  //
  // pushNotification.setSentToAndroid(new Date());
  //
  // log.debug("Response after sending push notification: {}",
  // phiPiiUtils.sanitizePhiPiiToString(response));
  // } catch (FirebaseMessagingException e) {
  // log.warn("FirebaseMessagingException", e);
  // } catch (Exception e) {
  // log.warn("Exception sending push notification", e);
  // }
  // return pushNotification;
  // }
  //
  //
  // // /*
  // // * TODO - set up for all device types
  // // */
  // // public PushNotification sendPushNtc(PushNotification pushNotification, Set<String> tokens) {
  // // try {
  // //
  // // log.debug("pushNotification={}", ObjectUtils.toJson(pushNotification));
  // //
  // // Map<String, String> customData = new HashMap<>();
  // // customData.put("navigationEnum", pushNotification.getNavigationEnum());
  // // customData.put("modelType", pushNotification.getModelType());
  // // customData.put("modelUuid", pushNotification.getModelUuid());
  // //
  // // WebpushNotification webNtc = WebpushNotification.builder()
  // // .setTitle(pushNotification.getTitle())
  // // .setBody(pushNotification.getDescription())
  // // .setIcon("")
  // // .build();
  // //
  // // AndroidNotification androidNtc = AndroidNotification.builder()
  // // .setTitle(pushNotification.getTitle())
  // // .setBody(pushNotification.getDescription())
  // // .setClickAction("FIREBASEPUSHNOTIFICATION")
  // // .build();
  // //
  // // MulticastMessage multipleMessage = MulticastMessage.builder()
  // // .putAllData(customData)
  // // .addAllTokens(new ArrayList<>(tokens))
  // // .setWebpushConfig(WebpushConfig.builder().setNotification(webNtc).build())
  // //
  // .setAndroidConfig(AndroidConfig.builder().setNotification(androidNtc).setCollapseKey("Test_Collapse_Key").setPriority(Priority.HIGH).build())
  // // .build();
  // //
  // // BatchResponse pushNtcResponse =
  // // FirebaseMessaging.getInstance(firebaseApp).sendMulticast(multipleMessage);
  // //
  // // pushNotification.setSentToAndroid(new Date());
  // //
  // // log.info("pushNtcResponse={}", ObjectUtils.toJson(pushNtcResponse));
  // // } catch (FirebaseMessagingException e) {
  // // log.warn("FirebaseMessagingException, msg: {}", e.getLocalizedMessage());
  // // e.printStackTrace();
  // // } catch (Exception e) {
  // // log.warn("Exception, msg: {}", e.getLocalizedMessage());
  // // e.printStackTrace();
  // // }
  // //
  // // return pushNotification;
  // // }

  @Override
  public void push(Notification ntc, Map<String, String> data) {
    // TODO Auto-generated method stub

  }


}
