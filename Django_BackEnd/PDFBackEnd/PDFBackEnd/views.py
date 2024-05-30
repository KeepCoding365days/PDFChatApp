from django.conf import settings
from django.http import JsonResponse, HttpResponse
from rest_framework.decorators import api_view
from rest_framework.status import HTTP_200_OK,HTTP_400_BAD_REQUEST
from .Transformer import *
import os

def generate_audio(request):
    if request.method=='GET':
        text=request.GET.get('text')
        if text:
            text_to_audio(text)
            return JsonResponse({'audio_url': 'sound.mp3'})
        else:
            return JsonResponse({'error': 'Text not provided'}, status=400)
    else:
        return JsonResponse({'error': 'Invalid request method'}, status=405)

def download_file(request, file_path):
    # Construct the absolute path to the file
    if request.method=="GET":
        file_absolute_path = file_path

        # Check if the file exists
        if os.path.exists(file_absolute_path):
            # Open the file in binary mode for reading
            with open(file_absolute_path, 'rb') as file:
                # Create an HTTP response with the file as content
                response = HttpResponse(file.read(), content_type='application/octet-stream')
                # Set the Content-Disposition header to make the browser download the file
                response['Content-Disposition'] = f'attachment; filename="{os.path.basename(file_absolute_path)}"'
                return response
        else:
            # If the file does not exist, return a 404 Not Found response
            return HttpResponse('File not found', status=404)
    else:
        return JsonResponse({'error': 'Invalid request method'}, status=405)

def generate_summary(request):
    if request.method=='GET':
        text=request.GET.get('text')
        if text:
            summary=summarize(text)
            return JsonResponse({'summary': summary})
        else:
            return JsonResponse({'error': 'Text not provided'}, status=400)
    else:
        return JsonResponse({'error': 'Invalid request method'}, status=405)