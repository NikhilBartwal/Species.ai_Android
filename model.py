import numpy as np
import streamlit as st
import tensorflow as tf

@st.cache
def load_model(category):
    tflite_model_path = f'Project/app/src/main/assets/{category}.tflite'
    interpreter = tf.lite.interpreter(tflite_model_path)
    interpreter.allocate_tensors()

    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    return interpreter, input_details, output_details
