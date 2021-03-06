(ns pst.message
  (require [pst.util :as pu]
           [pst.attachment :as pat]
           [pst.recipient :as pr]))


(defrecord Message [java-object]
  pu/PSTMessage
  (to-dict [this] (dissoc this :java-object)))

(defn message [m]
  (merge
   (->Message m)
   (pu/obj->map m
             :rtf-body                                 .getRTFBody
             :importance                               .getImportance
             :message-class                            .getMessageClass
             :subject                                  .getSubject
             :client-submit-time                       .getClientSubmitTime
             :received-by-name                         .getReceivedByName
             :received-by-address-type                 .getReceivedByAddressType
             :received-by-address                      .getReceivedByAddress
             :sent-representing-name                   .getSentRepresentingName
             :sent-representing-address-type           .getSentRepresentingAddressType
             :sent-representing-email-address          .getSentRepresentingEmailAddress

             ;; "This is basically the subject from which Fwd:, Re,
             ;; etc has been removed"
             :conversation-topic                       .getConversationTopic

             :transport-message-headers                .getTransportMessageHeaders
             :is-read                                  .isRead
             :is-unmodified                            .isUnmodified
             :is-submitted                             .isSubmitted
             :is-unset                                 .isUnsent
             :has-attachments                          .hasAttachments
             :is-from-me                               .isFromMe
             :is-associated                            .isAssociated
             :is-resent                                .isResent
             :acknowledgement-mode                     .getAcknowledgementMode
             :delivery-report-requested                .getOriginatorDeliveryReportRequested
             :priority                                 .getPriority
             :read-receipt-requested                   .getReadReceiptRequested
             :recipient-reassignment-prohibited        .getRecipientReassignmentProhibited

             ;; sensitivity of the message before being replied to or
             ;; forwarded
             :original-sensitivity                     .getOriginalSensitivity
             ;; sender's opinion of the sensitivity of an email
             ;; 0 = None 1 = Personal 2 = Private
             ;; 3 = Company Confidential
             :sensitivity                              .getSensitivity

             :pid-tag-sent-representing-search-key     .getPidTagSentRepresentingSearchKey
             :recvd-representing-name                  .getRcvdRepresentingName
             :original-subject                         .getOriginalSubject
             :reply-recipient-names                    .getReplyRecipientNames
             :message-to-me                            .getMessageToMe    ;; i'm in recip
             :message-cc-me                            .getMessageCcMe    ;; i'm in cc
             :message-recip-me                         .getMessageRecipMe ;; or message-to-me message-cc-me)
             :response-requested                       .getResponseRequested

             :original-display-bcc                     .getOriginalDisplayBcc
             :original-display-cc                      .getOriginalDisplayCc
             :original-display-to                      .getOriginalDisplayTo
             :recvd-representing-address-type          .getRcvdRepresentingAddrtype
             :recvd-representing-email-address         .getRcvdRepresentingEmailAddress

             :non-recept-notification-requested        .isNonReceiptNotificationRequested
             :originator-non-delivery-report-requested .isOriginatorNonDeliveryReportRequested
             :recipient-type                           .getRecipientType
             :reply-requested                          .isReplyRequested
             :sender-entry-id                          .getSenderEntryId
             :sender-name                              .getSenderName
             :sender-address-type                      .getSenderAddrtype
             :sender-email-address                     .getSenderEmailAddress

             :message-size                             .getMessageSize
             :internat-article-number                  .getInternetArticleNumber
             :primary-send-account                     .getPrimarySendAccount
             :next-send-account                        .getNextSendAcct
             :comp-name-postfix                        .getURLCompNamePostfix ;; ???
             :object-type                              .getObjectType ;; ???
             :delete-after-submit                      .getDeleteAfterSubmit
             :responsibility                           .getResponsibility
             :rtf-in-sync                              .isRTFInSync
             :url-comp-name-set                        .isURLCompNameSet
             :display-bcc                              .getDisplayBCC
             :display-cc                               .getDisplayCC
             :display-to                               .getDisplayTo
             :message-delivery-time                    .getMessageDeliveryTime

             ;; content
             :body                                     .getBody ;; plain text
             :body-prefix                              .getBodyPrefix ;; plain text body prefix
             :rtf-body-crc                             .getRTFSyncBodyCRC
             :rtf-body-count                           .getRTFSyncBodyCount
             :rtf-body-tag                             .getRTFSyncBodyTag
             :rtf-prefix-count                         .getRTFSyncPrefixCount
             :rtf-trailing-count                       .getRTFSyncTrailingCount
             :body-html                                .getBodyHTML
             :rfc-message-id                           .getInternetMessageId
             :in-reply-to-id                           .getInReplyToId
             :return-path                              .getReturnPath
             :icon-index                               .getIconIndex

             ;; action -- semi-unknown
             :action-flag                              .getActionFlag
             :forwarded                                .hasForwarded
             :replied                                  .hasReplied
             :action-date                              .getActionDate
             :disable-full-fidelity                    .getDisableFullFidelity
             :url-comp-name                            .getURLCompName ;; "Contains the .eml file name"
             :attr-hidden                              .getAttrHidden
             :attr-system                              .getAttrSystem
             :attr-readonly                            .getAttrReadonly

             :number-of-recipients                     .getNumberOfRecipients

             ;; tasks
             :task-start-date                          .getTaskStartDate
             :task-due-date                            .getTaskDueDate
             :reminder-set                             .getReminderSet
             :reminder-delta                           .getReminderDelta

             ;; categories
             :color-categories                         .getColorCategories

             :attachment-count                         .getNumberOfAttachments

             ;; recip
             :recipients-string                        .getRecipientsString
             )))

;; these exist in libpst in git, but not in a tagged release.
;;           :body-type       .getNativeBodyType
;;           :conversation-id .getConversationId

(defn nth-attachment
  "Given a message and an attachment index, return that attachment
  from the message. Return nil if the index is out of range"
  [m n] (if (> n (- (:attachment-count m) 1))
          nil
          (.getAttachment (:java-object m) n)))

(defn attachments
  "Given a message, return a lazy seq of its Attachments"
  ([^Message m]   (attachments m 0))
  ([^Message m n] (let [current-attachment (nth-attachment m n)]
                    (if (nil? current-attachment)
                      nil
                      (lazy-seq (cons (pat/attachment current-attachment)
                                      (attachments m (inc n))))))))

(defn nth-recipient
  [m n] (if (> n (- (:number-of-recipients m) 1))
          nil
          (.getRecipient (:java-object m) n)))

(defn recipients
  "Given a message, return a lazy seq of its Recipients"
  ([^Message m] (recipients m 0))
  ([^Message m n] (let [current-recipient (nth-recipient m n)]
                    (if (nil? current-recipient)
                      nil
                      (lazy-seq (cons (pr/recipient current-recipient)
                                      (recipients m (inc n))))))))
