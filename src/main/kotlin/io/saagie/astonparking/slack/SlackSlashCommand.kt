package io.saagie.astonparking.slack

import io.saagie.astonparking.domain.Proposition
import io.saagie.astonparking.service.DrawService
import io.saagie.astonparking.service.UserService
import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.Message
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.ArrayList


@RestController
class SlackSlashCommand(
        val userService: UserService,
        val drawService: DrawService,
        val slackBot: SlackBot
) {

    @Value("\${url}")
    private val url: String? = null

    @RequestMapping(value = "/slack/command",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveHelpCommand(@RequestParam("token") token: String,
                             @RequestParam("team_id") teamId: String,
                             @RequestParam("team_domain") teamDomain: String,
                             @RequestParam("channel_id") channelId: String,
                             @RequestParam("channel_name") channelName: String,
                             @RequestParam("user_id") userId: String,
                             @RequestParam("user_name") userName: String,
                             @RequestParam("command") command: String,
                             @RequestParam("text") text: String,
                             @RequestParam("response_url") responseUrl: String): RichMessage {
        val richMessage = RichMessage("---Aston Parking : Available commands--- ")
        val attachments = arrayOf(
                Attachment().apply {
                    setText("/ap-command : this list of all available commands")
                },
                Attachment().apply {
                    setText("/ap-register : to register you as a new member of Aston Parking")
                },
                Attachment().apply {
                    setText("/ap-profile : to display your AstonParking profile")
                },
                Attachment().apply {
                    setText("/ap-attribution : to display attribution for the current and the next week")
                },
                Attachment().apply {
                    setText("/ap-today : to see today spots attribution")
                },
                Attachment().apply {
                    setText("/ap-pick-today : to pick a spot today 'if available' = /ap-pick TODAY")
                },
                Attachment().apply {
                    setText("/ap-inactive-profile : to disable your AstonParking profile")
                },
                Attachment().apply {
                    setText("/ap-active-profile : to enable your AstonParking profile")
                },
                Attachment().apply {
                    setText("/ap-accept : to accept all attribution")
                },
                Attachment().apply {
                    setText("/ap-decline : to decline all attribution")
                },
                Attachment().apply {
                    setText("/ap-release dd/MM : to release an accepted spot for the specified date (day/month)")
                },
                Attachment().apply {
                    setText("/ap-pick dd/MM : to pick a free spot for the specified date (day/month)")
                },
                Attachment().apply {
                    setText("/ap-planning : to display personnal planning for the current and the next week")
                },
                Attachment().apply {
                    setText("/ap-request dd/MM : to make a request for a spot on a desired date (only one per user and double debit when selected)")
                }
        )
        richMessage.attachments = attachments


        return richMessage.encodedMessage()
    }

    @RequestMapping(value = "/slack/register",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveRegisterCommand(@RequestParam("token") token: String,
                                 @RequestParam("team_id") teamId: String,
                                 @RequestParam("team_domain") teamDomain: String,
                                 @RequestParam("channel_id") channelId: String,
                                 @RequestParam("channel_name") channelName: String,
                                 @RequestParam("user_id") userId: String,
                                 @RequestParam("user_name") userName: String,
                                 @RequestParam("command") command: String,
                                 @RequestParam("text") text: String,
                                 @RequestParam("response_url") responseUrl: String): RichMessage {
        val registerUser = userService.registerUser(userName, userId)
        val richMessage = RichMessage("Register : ${userName}")
        val attachments = arrayOfNulls<Attachment>(1)
        attachments[0] = Attachment()
        attachments[0]!!.setText("Welcome on Aston Parking ${userName}. Please log in on the website : ${url} to complete your registration.")
        if (!registerUser) {
            attachments[0]!!.setText("You are already registred on AstonParking. Use /ap-profile to see your profile.")
        }
        richMessage.attachments = attachments


        return richMessage.encodedMessage()
    }

    @RequestMapping(value = "/slack/profile/active",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveActiveProfileCommand(@RequestParam("token") token: String,
                                      @RequestParam("team_id") teamId: String,
                                      @RequestParam("team_domain") teamDomain: String,
                                      @RequestParam("channel_id") channelId: String,
                                      @RequestParam("channel_name") channelName: String,
                                      @RequestParam("user_id") userId: String,
                                      @RequestParam("user_name") userName: String,
                                      @RequestParam("command") command: String,
                                      @RequestParam("text") text: String,
                                      @RequestParam("response_url") responseUrl: String): RichMessage {
        userService.changeStatus(userId, true)
        val richMessage = RichMessage("Activate Profile : ${userName}")
        val attachments = arrayOfNulls<Attachment>(1)
        attachments[0] = Attachment()
        attachments[0]!!.setText("Your profile is now active.")
        richMessage.attachments = attachments

        return richMessage.encodedMessage()
    }

    @RequestMapping(value = "/slack/profile/inactive",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveInactiveProfileCommand(@RequestParam("token") token: String,
                                        @RequestParam("team_id") teamId: String,
                                        @RequestParam("team_domain") teamDomain: String,
                                        @RequestParam("channel_id") channelId: String,
                                        @RequestParam("channel_name") channelName: String,
                                        @RequestParam("user_id") userId: String,
                                        @RequestParam("user_name") userName: String,
                                        @RequestParam("command") command: String,
                                        @RequestParam("text") text: String,
                                        @RequestParam("response_url") responseUrl: String): RichMessage {
        userService.changeStatus(userId, false)
        val richMessage = RichMessage("Desactivate Profile : ${userName}")
        val attachments = arrayOfNulls<Attachment>(1)
        attachments[0] = Attachment()
        attachments[0]!!.setText("Your profile is now inactive.")
        richMessage.attachments = attachments

        return richMessage.encodedMessage()
    }

    @RequestMapping(value = "/slack/profile",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveProfileCommand(@RequestParam("token") token: String,
                                @RequestParam("team_id") teamId: String,
                                @RequestParam("team_domain") teamDomain: String,
                                @RequestParam("channel_id") channelId: String,
                                @RequestParam("channel_name") channelName: String,
                                @RequestParam("user_id") userId: String,
                                @RequestParam("user_name") userName: String,
                                @RequestParam("command") command: String,
                                @RequestParam("text") text: String,
                                @RequestParam("response_url") responseUrl: String): RichMessage {


        try {
            val user = userService.get(userId)
            val richMessage = RichMessage("Profile : ${user.username}")
            if (!user.activated) {
                richMessage.text = richMessage.text + """
                    - :warning: *PROFILE NOT ACTIVATED* :warning: -
                    Please log in AstonParking to activate your profile (${url})
                    """.trimIndent()
            }
            val attachments = arrayOf(
                    Attachment().apply {
                        setText("Username : ${user.username}")
                    },
                    Attachment().apply {
                        setText("Email : ${user.email}")
                    },
                    Attachment().apply {
                        setText("Attribution : ${user.attribution}")
                    },
                    Attachment().apply {
                        setText("Status :  ${user.status()}")
                    }
            )
            richMessage.attachments = attachments

            return richMessage.encodedMessage()
        } catch (e: Exception) {
            val richMessage = RichMessage("The profile : ${userName} doesn't exist. Type /ap-register to create your profile.")
            return richMessage.encodedMessage()
        }

    }

    @RequestMapping(value = "/slack/attribution",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveAttributionCommand(@RequestParam("token") token: String,
                                    @RequestParam("team_id") teamId: String,
                                    @RequestParam("team_domain") teamDomain: String,
                                    @RequestParam("channel_id") channelId: String,
                                    @RequestParam("channel_name") channelName: String,
                                    @RequestParam("user_id") userId: String,
                                    @RequestParam("user_name") userName: String,
                                    @RequestParam("command") command: String,
                                    @RequestParam("text") text: String,
                                    @RequestParam("response_url") responseUrl: String): Message {

        val propositions = drawService.getAllPropositions()
        val currentSchedules = drawService.getCurrentSchedules()
        val nextSchedules = drawService.getNextSchedules()
        val users = userService.getAll()

        val message = Message("*******************\n")
        message.text += slackBot.generateTextForSchedule(currentSchedules, null)
        message.text += "*******************\n"
        message.text += "*Next week\n\n"
        message.text += "_Not accepted yet_\n"
        message.text += slackBot.generateTextForPropositions(propositions!!, users, null)
        message.text += "\n_Accepted_\n"
        message.text += slackBot.generateTextForSchedule(nextSchedules, null)
        message.text += "*******************\n"


        return message
    }


    @RequestMapping(value = "/slack/accept",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveAcceptCommand(@RequestParam("token") token: String,
                               @RequestParam("team_id") teamId: String,
                               @RequestParam("team_domain") teamDomain: String,
                               @RequestParam("channel_id") channelId: String,
                               @RequestParam("channel_name") channelName: String,
                               @RequestParam("user_id") userId: String,
                               @RequestParam("user_name") userName: String,
                               @RequestParam("command") command: String,
                               @RequestParam("text") text: String,
                               @RequestParam("response_url") responseUrl: String): Message {


        val message = Message("OK, your propositions are accepted.")
        if (!drawService.acceptProposition(userId)) {
            message.text = "You have no proposition for the next week."
        }

        return message
    }

    @RequestMapping(value = "/slack/decline",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveDeclineCommand(@RequestParam("token") token: String,
                                @RequestParam("team_id") teamId: String,
                                @RequestParam("team_domain") teamDomain: String,
                                @RequestParam("channel_id") channelId: String,
                                @RequestParam("channel_name") channelName: String,
                                @RequestParam("user_id") userId: String,
                                @RequestParam("user_name") userName: String,
                                @RequestParam("command") command: String,
                                @RequestParam("text") text: String,
                                @RequestParam("response_url") responseUrl: String): Message {


        drawService.declineProposition(userId)

        val message = Message("You've declined all your propositions")
        return message
    }

    @RequestMapping(value = "/slack/release",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveReleaseCommand(@RequestParam("token") token: String,
                                @RequestParam("team_id") teamId: String,
                                @RequestParam("team_domain") teamDomain: String,
                                @RequestParam("channel_id") channelId: String,
                                @RequestParam("channel_name") channelName: String,
                                @RequestParam("user_id") userId: String,
                                @RequestParam("user_name") userName: String,
                                @RequestParam("command") command: String,
                                @RequestParam("text") text: String,
                                @RequestParam("response_url") responseUrl: String): Message {

        drawService.release(userId, text)

        val message = Message("You have release the spot for the day (${text}) and it will be reaffected to another. Thanks.")
        return message

    }

    @RequestMapping(value = "/slack/pick",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceivePickCommand(@RequestParam("token") token: String,
                             @RequestParam("team_id") teamId: String,
                             @RequestParam("team_domain") teamDomain: String,
                             @RequestParam("channel_id") channelId: String,
                             @RequestParam("channel_name") channelName: String,
                             @RequestParam("user_id") userId: String,
                             @RequestParam("user_name") userName: String,
                             @RequestParam("command") command: String,
                             @RequestParam("text") text: String,
                             @RequestParam("response_url") responseUrl: String): Message {

        try {
            val spot = drawService.pick(userId, text)
            val message = Message("You have pick the ${spot} for the day (${text}) .")
            return message
        } catch (iae: IllegalArgumentException) {
            return Message(iae.message)
        }

    }

    @RequestMapping(value = "/slack/pick-today",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceivePickTodayCommand(@RequestParam("token") token: String,
                             @RequestParam("team_id") teamId: String,
                             @RequestParam("team_domain") teamDomain: String,
                             @RequestParam("channel_id") channelId: String,
                             @RequestParam("channel_name") channelName: String,
                             @RequestParam("user_id") userId: String,
                             @RequestParam("user_name") userName: String,
                             @RequestParam("command") command: String,
                             @RequestParam("text") text: String,
                             @RequestParam("response_url") responseUrl: String): Message {

        try {
            val spot = drawService.pick(userId, LocalDate.now())
            val message = Message("You have pick the ${spot} for today.")
            return message
        } catch (iae: IllegalArgumentException) {
            return Message(iae.message)
        }

    }

    @RequestMapping(value = "/slack/today",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveTodayCommand(@RequestParam("token") token: String,
                                    @RequestParam("team_id") teamId: String,
                                    @RequestParam("team_domain") teamDomain: String,
                                    @RequestParam("channel_id") channelId: String,
                                    @RequestParam("channel_name") channelName: String,
                                    @RequestParam("user_id") userId: String,
                                    @RequestParam("user_name") userName: String,
                                    @RequestParam("command") command: String,
                                    @RequestParam("text") text: String,
                                    @RequestParam("response_url") responseUrl: String): Message {

        val currentSchedules = drawService.getCurrentSchedules().filter { it.date == LocalDate.now() }

        val message = Message("*******************\n")
        message.text += "\nToday :\n"
        message.text += slackBot.generateTextForSchedule(currentSchedules, null)
        message.text += "*******************\n"


        return message
    }

    @RequestMapping(value = "/slack/planning",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceivePlanningCommand(@RequestParam("token") token: String,
                              @RequestParam("team_id") teamId: String,
                              @RequestParam("team_domain") teamDomain: String,
                              @RequestParam("channel_id") channelId: String,
                              @RequestParam("channel_name") channelName: String,
                              @RequestParam("user_id") userId: String,
                              @RequestParam("user_name") userName: String,
                              @RequestParam("command") command: String,
                              @RequestParam("text") text: String,
                              @RequestParam("response_url") responseUrl: String): Message {

        val propositions = drawService.getAllPropositions()!!.filter { it.userId ==  userId}
        val currentSchedules = drawService.getCurrentSchedules()
        val nextSchedules = drawService.getNextSchedules()
        val users = userService.getAll()

        val message = Message("*******************\n")
        message.text += slackBot.generateTextForSchedule(currentSchedules, userId)
        message.text += "*******************\n"
        message.text += "*Next week\n\n"
        message.text += "_Not accepted yet_\n"
        message.text += slackBot.generateTextForPropositions(propositions as ArrayList<Proposition>, users,userId)
        message.text += "\n_Accepted_\n"
        message.text += slackBot.generateTextForSchedule(nextSchedules,userId)
        message.text += "*******************\n"


        return message
    }

    @RequestMapping(value = "/slack/request",
            method = arrayOf(RequestMethod.POST),
            consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    fun onReceiveRequestCommand(@RequestParam("token") token: String,
                                 @RequestParam("team_id") teamId: String,
                                 @RequestParam("team_domain") teamDomain: String,
                                 @RequestParam("channel_id") channelId: String,
                                 @RequestParam("channel_name") channelName: String,
                                 @RequestParam("user_id") userId: String,
                                 @RequestParam("user_name") userName: String,
                                 @RequestParam("command") command: String,
                                 @RequestParam("text") text: String,
                                 @RequestParam("response_url") responseUrl: String): Message {

        try {
            drawService.request(userId, text)
            val message = Message("Your request for a spot for the day (${text}) is recorded.")
            return message
        } catch (iae: IllegalArgumentException) {
            return Message(iae.message)
        }
    }
}