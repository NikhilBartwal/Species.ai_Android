import os
import numpy as np
import tensorflow as tf
import streamlit as st
from model import *
from utils import *

def display_homepage():
    welcome_container = st.beta_container()
    info_container = st.beta_container()

    with welcome_container:
        welcome_text, app_logo = st.beta_columns([2,1])
        with welcome_text:
            st.title('Welcome to Species.AI!')
            st.subheader('Made with :heart: by - Nikhil Bartwal')
        with app_logo:
            st.image('logo.png')

    uploaded_file = st.file_uploader('Please upload an image here-> ')

    if uploaded_file is not None:
        image = process_image(uploaded_file)
        category, fun_mode = display_image(image)
        model, input_details, output_details = load_model(category)
        st.write(input_details, output_details)
    else:
        with info_container:
            animals_count, flowers_count, birds_count = st.beta_columns(3)
            with animals_count:
                st.subheader(':dog: 30+ Animals')
            with flowers_count:
                st.subheader(':maple_leaf: 100+ Flowers')
            with birds_count:
                st.subheader(':bird: 200+ Birds')

            st.subheader('Our mother earth has blessed us with such a huge variety \
                    of flora and fauna')

def deploy_speciesai():
    options = ['Homepage', 'Start the App']
    option = st.sidebar.selectbox('Please choose an option: ', options)
    if option == options[0]:
        display_homepage()
    elif option == options[1]:
        st.write('Time to upload a picture!')

if __name__ == '__main__':
    deploy_speciesai()
