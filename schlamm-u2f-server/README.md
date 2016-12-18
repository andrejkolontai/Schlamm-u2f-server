My very own version of a u2f server. I could have used the 
reference implementation from Google. But I wanted to do it
myself. 

Compared to Google's reference implementation, this one has
no dependencies to any other libraries. However, it requires
Java 8. 

Another difference is that the central "Server" object is a
purely functional object with no internal state and no 
dependencies to other stateful objects. You'll have to glue
it to your app yourself. But you can decide how you do it, be
it CDI, Spring or whatever. 

This version is not ready for anything but experiments. Some
crucial security tests are missing. Basically, it's only able
to do anything at all. 

And there is no documentation at all at the moment. 

License: Have not decided yet.But actually, I don't care if
anyone uses or modifies it. And, of course, I do not take
any responsibilty for anything. 