package notifiers;

import interfaces.INotifier;

public class SimpleNotifierFactory extends NotifierFactory {

    @Override
    public INotifier createEmailNotifier() {
        return new EmailNotifier();
    }

    @Override
    public INotifier createDiscordNotifier() {
        return new DiscordNotifier();
    }

    @Override
    public INotifier createPushNotifier() {
        return new PushNotifier();
    }
}
