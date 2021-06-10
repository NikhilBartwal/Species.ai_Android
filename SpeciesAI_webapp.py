import os
import numpy as np
import tensorflow as tf
import streamlit as st

def display_homepage():
    welcome_text, app_logo = st.beta_columns([2,1])
    with welcome_text:
        st.title('Welcome to Species.AI!')
        st.subheader('Made with :heart: by - Nikhil Bartwal')
    with app_logo:
        st.image('logo.png')

    animals_count, flowers_count, birds_count = st.beta_columns(3)
    with animals_count:
        st.subheader(':dog: 30+ Animals')
    with flowers_count:
        st.subheader(':maple_leaf: 100+ Flowers')
    with birds_count:
        st.subheader(':bird: 200+ Birds')

    st.subheader('Our mother earth :earth_asia: has blessed us with such a huge variety \
            of flora :tulip::rose::sunflower::hibiscus: and fauna :chicken::cat: \
            :elephant::sheep:')

    with st.beta_expander('Get model information'):
        st.subheader("Model description comes here")

def deploy_speciesai():
    display_homepage()
    options = ['Homepage', 'Take picture', 'Upload Image']
    option = st.sidebar.selectbox('Please choose an option: ', options)
    if option == options[1]:
        st.write('Lets take a picture. Cheeseeeeeee! :cheese:')
    elif option == options[2]:
        st.write('Time to upload a picture!')

if __name__ == '__main__':
    deploy_speciesai()
