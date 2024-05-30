import openai
import pyttsx3
from pydub import AudioSegment
from openai import OpenAI
client= openai.Client(api_key="OpenAIAPIkeyHere")
def text_to_audio(text):
    response=client.audio.speech.create(
        model="tts-1",
        voice="alloy",
        input=text
    )
    response.stream_to_file("download_file/sound.mp3")

def summarize(text):
    response=client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[
            {"role":"system","content":"You are an AI summarizer. You need to summarize given text"},
            {"role":"user","content":text}
        ]
    )
    return response.choices[0].message.content

def dummy(text):
    engine = pyttsx3.init()

    # Adjust voice characteristics based on emot
    engine.setProperty('rate', 150)  # Default rate
    engine.setProperty('pitch', 150)  # Default pitch
    engine.setProperty('volume', 1.0)  # Default volume
    engine.save_to_file(text, "sound.wav")
    sound = AudioSegment.from_wav("sound.wav")
    sound.export("sound.mp3", format="mp3")
